package com.example.ref.service.admin;

import com.example.ref.config.redis.RedisPublisher;
import com.example.ref.dto.BoardNoticeDto;
import com.example.ref.dto.FileDto;
import com.example.ref.dto.RefreshSocketDto;
import com.example.ref.entity.Admin;
import com.example.ref.entity.BoardColor;
import com.example.ref.entity.BoardNotice;
import com.example.ref.entity.BoardNoticeImage;
import com.example.ref.entity.NoticeCategory;
import com.example.ref.repository.AdminRepository;
import com.example.ref.repository.BoardColorRepository;
import com.example.ref.repository.BoardNoticeImageRepository;
import com.example.ref.repository.BoardNoticeRepository;
import com.example.ref.repository.NoticeCategoryRepository;
import com.example.ref.request.admin.board.notice.AddBoardNoticeRequestDto;
import com.example.ref.request.admin.board.notice.GetBoardNoticeListRequestDto;
import com.example.ref.request.admin.board.notice.ModifyBoardNoticeRequestDto;
import com.example.ref.rules.BoardImageFileType;
import com.example.ref.rules.FileType;
import com.example.ref.rules.RefreshSocketType;
import com.example.ref.util.AuthUtils;
import com.example.ref.util.S3FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardNoticeService extends BaseBoard<BoardNoticeDto, AddBoardNoticeRequestDto, GetBoardNoticeListRequestDto, ModifyBoardNoticeRequestDto> {

    private final BoardNoticeRepository boardNoticeRepository;
    private final BoardNoticeImageRepository boardNoticeImageRepository;
    private final AdminRepository adminRepository;
    private final S3FileUtils s3FileUtils;
    private final ObjectMapper objectMapper;
    private final BoardColorRepository boardColorRepository;
    private final NoticeCategoryRepository noticeCategoryRepository;
    private final RedisPublisher redisPublisher;
    private final ChannelTopic noticeChannelTopic;


    @Value("${spring.servlet.multipart.location}")
    private String filePath;

    @Value("${path.gov-ncp-location}")
    private String govNcpLocation;


    public List<BoardColor> getBoardColorList() {
        return boardColorRepository.findAll();
    }

    @Override
    public void addBoard(AddBoardNoticeRequestDto request) throws IOException {
        String userId = AuthUtils.getUserId();
        Admin admin = adminRepository.findByAdminId(userId);

        NoticeCategory noticeCategory = noticeCategoryRepository.findById(request.getCategoryIdx()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다."));

        BoardColor boardColor = null;

        if(request.getColorIdx() != null ) {
            boardColor = boardColorRepository.findById(request.getColorIdx()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 색상 입니다."));
        }

        BoardNotice boardNotice = BoardNotice.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .admin(admin)
            .noticeCategory(noticeCategory)
            .boardColor(boardColor)
            .build();

        boardNoticeRepository.save(boardNotice);

        // S3 파일 업로드
        if (request.getImageFile01() != null) {
            uploadBoardFile(request.getImageFile01(), boardNotice, FileType.BOARD_NOTICE_IMAGE_01);
        }
        if (request.getImageFile02() != null) {
            uploadBoardFile(request.getImageFile02(), boardNotice, FileType.BOARD_NOTICE_IMAGE_02);
        }
        if (request.getImageFile03() != null) {
            uploadBoardFile(request.getImageFile03(), boardNotice, FileType.BOARD_NOTICE_IMAGE_03);
        }
        if (request.getImageFile04() != null) {
            uploadBoardFile(request.getImageFile04(), boardNotice, FileType.BOARD_NOTICE_IMAGE_04);
        }
        if (request.getImageFile05() != null) {
            uploadBoardFile(request.getImageFile05(), boardNotice, FileType.BOARD_NOTICE_IMAGE_05);
        }


        try {
            // 게시판 작성시 STOMP 메시지 전송(웹소켓) 리프레시 하라고
            RefreshSocketDto refreshSocketDto = RefreshSocketDto.builder()
                .type(RefreshSocketType.NOTICE_TYPE.getType())
                .action(RefreshSocketType.CREATE_ACTION.getType())
                .message("A new post has been registered.")
                .dateTime(LocalDateTime.now())
                .build();

            // Dto to Json
            String refreshSocketDtoJson = objectMapper.writeValueAsString(refreshSocketDto);
            redisPublisher.publish(noticeChannelTopic, refreshSocketDtoJson);
        } catch (Exception e) {
            log.error("게시판 작성시 STOMP 메시지 전송(웹소켓) 리프레시 하라고 에러 발생 : {}", e.getMessage());
        }

    }


    public void deleteBoardImage(Long id) {
        BoardNoticeImage boardNoticeImage = boardNoticeImageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지 입니다."));

        boardNoticeImageRepository.delete(boardNoticeImage);

        s3FileUtils.deleteFile(boardNoticeImage.getFilePath());
    }


    private void uploadBoardFile(MultipartFile imageFile, BoardNotice boardNotice, FileType fileType) throws IOException {

        FileDto fileDto = s3FileUtils.uploadFile(govNcpLocation, imageFile, 5L, FileType.BOARD_NOTICE_IMAGE);

        if (fileDto != null) {
            BoardNoticeImage boardNoticeImage = BoardNoticeImage.builder()
                .boardNotice(boardNotice)
                .originFileName(fileDto.getOriginFileName())
                .savedFileName(fileDto.getSavedFileName())
                .fileExtension(fileDto.getFileExtension())
                .filePath(fileDto.getFilePath())
                .url(fileDto.getUrl())
                .type(fileType.getPath())
                .build();

            boardNoticeImageRepository.save(boardNoticeImage);
        }
    }


    @Override
    public BoardNoticeDto getBoard(Long id) {

        BoardNotice boardNotice = boardNoticeRepository.findBoardNoticeAndImageById(id);
        if(boardNotice == null) throw new IllegalArgumentException("존재하지 않는 게시글 입니다.");

        return BoardNoticeDto.convertToDto(boardNotice);
    }

    @Override
    public Page<BoardNoticeDto> getBoardList(Pageable pageable, GetBoardNoticeListRequestDto request) {

        Page<BoardNotice> boardNoticeList = boardNoticeRepository.findAllByTitleContainingAndCreatedAtBetween(
            pageable,
            request.getKeyword(),
            request.getStartDate().atStartOfDay(),
            request.getEndDate().atTime(23, 59, 59)
        );

        return boardNoticeList.map(BoardNoticeDto::convertToDto);
    }

    @Override
    public void modifyBoard(ModifyBoardNoticeRequestDto request) throws IOException {

        BoardNotice boardNotice = boardNoticeRepository.findBoardNoticeAndImageById(request.getBoardNoticeIdx());

        if(boardNotice == null){
            throw new IllegalArgumentException("존재하지 않는 게시글 입니다.");
        }

        NoticeCategory noticeCategory = noticeCategoryRepository.findById(request.getCategoryIdx()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다."));

        BoardColor boardColor = null;

        if(request.getColorIdx() != null ) {
            boardColor = boardColorRepository.findById(request.getColorIdx()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 색상 입니다."));
        }

        boardNotice.modifyBoardNotice(request);
        boardNotice.setNoticeCategory(noticeCategory);
        boardNotice.setBoardColor(boardColor);

        for(BoardImageFileType boardImageFileType : BoardImageFileType.values()) {
            processImageFile(boardNotice, request, boardImageFileType);
        }

        try{
            // 게시글 수정시 STOMP 메시지 전송(웹소켓) 리프레시 하라고
            RefreshSocketDto refreshSocketDto = RefreshSocketDto.builder()
                .type(RefreshSocketType.NOTICE_TYPE.getType())
                .action(RefreshSocketType.UPDATE_ACTION.getType())
                .message("A post has been modified.")
                .dateTime(LocalDateTime.now())
                .build();

            // Dto to Json
            String refreshSocketDtoJson = objectMapper.writeValueAsString(refreshSocketDto);
            redisPublisher.publish(noticeChannelTopic, refreshSocketDtoJson);
        } catch (Exception e) {
            log.error("게시글 수정시 STOMP 메시지 전송(웹소켓) 리프레시 하라고 에러 발생 : {}", e.getMessage());
        }

    }

    private void processImageFile(BoardNotice boardNotice, ModifyBoardNoticeRequestDto request, BoardImageFileType boardImageFileType)
        throws IOException {
        if (request.getImageFile01() != null && boardImageFileType.getPath().equals(FileType.BOARD_NOTICE_IMAGE_01.getPath())) {
            handleImageFile(boardNotice, request.getImageFile01(), FileType.BOARD_NOTICE_IMAGE_01);
        }
        if (request.getImageFile02() != null && boardImageFileType.getPath().equals(FileType.BOARD_NOTICE_IMAGE_02.getPath())) {
            handleImageFile(boardNotice, request.getImageFile02(), FileType.BOARD_NOTICE_IMAGE_02);
        }
        if (request.getImageFile03() != null && boardImageFileType.getPath().equals(FileType.BOARD_NOTICE_IMAGE_03.getPath())) {
            handleImageFile(boardNotice, request.getImageFile03(), FileType.BOARD_NOTICE_IMAGE_03);
        }
        if (request.getImageFile04() != null && boardImageFileType.getPath().equals(FileType.BOARD_NOTICE_IMAGE_04.getPath())) {
            handleImageFile(boardNotice, request.getImageFile04(), FileType.BOARD_NOTICE_IMAGE_04);
        }
        if (request.getImageFile05() != null && boardImageFileType.getPath().equals(FileType.BOARD_NOTICE_IMAGE_05.getPath())) {
            handleImageFile(boardNotice, request.getImageFile05(), FileType.BOARD_NOTICE_IMAGE_05);
        }
        // 나머지 이미지 파일에 대한 처리도 추가
    }

    private void handleImageFile(BoardNotice boardNotice, MultipartFile imageFile, FileType fileType)
        throws IOException {

        BoardNoticeImage boardNoticeImage = boardNoticeImageRepository.findByBoardNoticeAndType(boardNotice, fileType.getPath());
        if(boardNoticeImage != null) {
            s3FileUtils.deleteFile(boardNoticeImage.getFilePath());
            boardNoticeImageRepository.deleteByBoardNoticeAndType(boardNotice, fileType.getPath());
            uploadBoardFile(imageFile, boardNotice, fileType);
        }
    }

    @Override
    public void deleteBoard(Long id) throws JsonProcessingException {

        BoardNotice boardNotice = boardNoticeRepository.findBoardNoticeAndImageById(id);

        if(boardNotice == null){
            throw new IllegalArgumentException("존재하지 않는 게시글 입니다.");
        }

        if(boardNotice.getBoardNoticeImageList() != null && !boardNotice.getBoardNoticeImageList().isEmpty()) {
            // 기존 이미지 DB 삭제
            boardNoticeImageRepository.deleteAll(boardNotice.getBoardNoticeImageList());
            // S3 기존 이미지 삭제
            boardNotice.getBoardNoticeImageList().forEach(boardNoticeImage -> s3FileUtils.deleteFile(boardNoticeImage.getFilePath()));
        }

        boardNoticeRepository.delete(boardNotice);

        try {
            // 게시글 삭제시 STOMP 메시지 전송(웹소켓) 리프레시 하라고
            RefreshSocketDto refreshSocketDto = RefreshSocketDto.builder()
                .type(RefreshSocketType.NOTICE_TYPE.getType())
                .action(RefreshSocketType.DELETE_ACTION.getType())
                .message("A post has been deleted.")
                .dateTime(LocalDateTime.now())
                .build();

            // Dto to Json
            String refreshSocketDtoJson = objectMapper.writeValueAsString(refreshSocketDto);
            redisPublisher.publish(noticeChannelTopic, refreshSocketDtoJson);
        } catch (Exception e) {
            log.error("게시글 삭제시 STOMP 메시지 전송(웹소켓) 리프레시 에러 발생 : {}", e.getMessage());
        }
    }
}

package com.ssafy.withssafy.api;

import com.ssafy.withssafy.dto.notification.NotificationResponseDto;
import com.ssafy.withssafy.dto.notification.NotificationRequestDto;
import com.ssafy.withssafy.service.notification.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공지사항 API
 * @author Jueun
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Api(tags = "공지사항 API")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping
    @ApiOperation(value = "모든 공지사항을 조회합니다.")
    public ResponseEntity<List<NotificationResponseDto>> findAll(){
        return new ResponseEntity<>(notificationService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "아이디에 맞는 공지사항을 조회합니다.")
    public ResponseEntity<NotificationResponseDto> findById(@RequestParam Long id){
        return new ResponseEntity<>(notificationService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation(value = "공지사항을 추가합니다.")
    public ResponseEntity<NotificationResponseDto> insert(@RequestBody NotificationRequestDto notificationRequestDto){
        return new ResponseEntity<>(notificationService.insert(notificationRequestDto), HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value = "보낸 아이디에 해당하는 공지사항을 수정합니다.")
    public ResponseEntity<NotificationResponseDto> update(@RequestBody NotificationRequestDto notificationRequestDto){
        return new ResponseEntity<>(notificationService.update(notificationRequestDto), HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value = "보낸 아이디에 해당하는 공지사항을 삭제합니다.")
    public ResponseEntity<NotificationResponseDto> delete(@RequestParam Long id){
        return new ResponseEntity<>(notificationService.delete(id), HttpStatus.OK);
    }
}
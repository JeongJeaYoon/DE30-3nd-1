package com.example.letterapp.controller;


import com.example.letterapp.model.LetterType;
import com.example.letterapp.dto.LetterTypeDto;
import com.example.letterapp.model.User;
import com.example.letterapp.service.LetterTypeService;
import com.example.letterapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/*
I-wish-001(받고 싶은 편지 선택하기 화면)
 */
@RestController
@RequestMapping("/api/lettertype")
public class LetterTypeController {

    private final LetterTypeService letterTypeService;
    private final UserService userService;

    @Autowired
    public LetterTypeController(LetterTypeService letterTypeService, UserService userService) {
        this.letterTypeService = letterTypeService;
        this.userService = userService;
    }

    // 회원가입 후 처음 들어간 받을 편지 화면
    @GetMapping("/user/{idx_user}/first")
    public ResponseEntity<LetterTypeDto> getLetterTypeForUser(@PathVariable("idx_user") Long idx_user) {
        // LetterTypeDto 객체 생성 및 값 설정
        LetterTypeDto letterTypeDto = new LetterTypeDto();
        letterTypeDto.setUserIdx(idx_user); // 사용자의 idx_user를 설정

        // 나머지 필드 설정
        letterTypeDto.setEmailSub(false);
        letterTypeDto.setEmail("");
        letterTypeDto.setRandomSub(false);
        letterTypeDto.setCategory(Arrays.asList(1, 2, 3));
        letterTypeDto.setComment("코멘트를 작성해주세요");

        return ResponseEntity.ok(letterTypeDto);
    }

    /*
     *   유저의 이메일 전송 여부, 랜덤여부 확인
     *   idx_user 값을 받음
     *   해당 유저의<User-사용자> 테이블에 있는 내용을 다 응답으로 줄 것(JSON 타입)
     */

// user 엔터티 전체를 JSON타입으로 주는 코드
    @GetMapping("/user/{idx_user}/select")
    public User getUserSelect(@PathVariable("idx_user") Long idx_user) {
        return userService.getUserById(idx_user);
    }

//    // user 엔터티에서 idx_user, email_sub, random_sub 3가지만 JSON타입으로 주는 코드
//    @GetMapping("/user/{idx_user}/select")
//    public Map<String, Object> getUserSelect(@PathVariable("idx_user") Long idx_user) {
//        User user = userService.getUserById(idx_user);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("idx_user", user.getIdx_user());
//        response.put("email_sub", user.isEmail_sub());
//        response.put("random_sub", user.isRandom_sub());
//
//        return response;
//    }

    /*
     * 유저의 편지 유형 선택 정보
     * idx_user 값을 받음
     * LetterType 엔터티내용 전체를 응답으로 주기(JSON 타입)
     */
    @GetMapping("/user/{idx_user}/lettertype")
    public List<LetterType> getLetterTypesForUser(@PathVariable("idx_user") Long idx_user) {
        return letterTypeService.getLetterTypesForUser(idx_user);
    }
/*
    받고 싶은 편지 선택하기 정보 변경 (저장하기 버튼 클릭시)
    idx_user, email_sub, email, random_sub, idx_letterType, category, comment를 입력 받아와서 갱신
 */

//    // user엔터티의 정보들 변경이 안됨...............악
//    @PostMapping("/change")
//    public ResponseEntity<LetterType> saveLetterType(@RequestBody LetterType letterType) {
//        try {
//            LetterType savedLetterType = letterTypeService.saveLetterType(letterType);
//            return ResponseEntity.ok(savedLetterType); // 200 OK
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(400).body(null); // 400 Bad Request
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(null); // 500 Internal Server Error
//        }
//    }


    // 전체 POST코드
    @PostMapping("/change")
    public ResponseEntity<LetterType> saveLetterType(@RequestBody LetterTypeDto letterTypeDto) {
        try {
            // userIdx를 사용하여 User 엔티티를 가져옴
            User user = userService.getUserById(letterTypeDto.getUserIdx());
            if (user == null) {
                return ResponseEntity.badRequest().body(null); // User가 없을 경우 예외 처리
            }

            // User 엔티티 업데이트
            user.setEmail_sub(letterTypeDto.isEmailSub());
            user.setEmail(letterTypeDto.getEmail());
            user.setRandom_sub(letterTypeDto.isRandomSub());

            // User 엔티티 저장
            userService.saveUser(user);

            // LetterTypeDto에서 필요한 필드를 가지고 LetterType 엔티티를 생성
            LetterType letterType = new LetterType();
            letterType.setCategory(letterTypeDto.getCategory());
            letterType.setComment(letterTypeDto.getComment());
            letterType.setUser(user);

            // LetterType 엔티티 저장
            LetterType savedLetterType = letterTypeService.saveLetterType(letterType);

            // 프론트에 보낼 response
            return ResponseEntity.ok(savedLetterType); // 200 OK - 업뎃완
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null); // 400 Bad Request - 업뎃안됨
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // 500 Internal Server Error - 업뎃안됨
        }
    }
}
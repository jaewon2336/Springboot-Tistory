let valid = {
    username: {
        state: false,
        msg: ""
    },
    password: {
        state: false,
        msg: ""
    }
}

// 전역적으로 유효성 검사
function validation() {
    if (valid.username.state && valid.password.state) {
        return true;
    } else {
        let errorMsgs = ``;

        errorMsgs += `${valid.username.msg}<br/>`;
        errorMsgs += `${valid.password.msg}<br/>`;

        $(".my_error_box").html(errorMsgs);
        $(".my_error_box").removeClass("my_hidden");
        return false;
    }
}

// 아이디 중복체크
$("#username").focusout(() => {
    usernameCheck(); // ajax 필요
});

// 비밀번호 동일체크
$("#password").focusout(() => {
    passwordSameCheck();
});

$("#password-check").focusout(() => {
    passwordSameCheck();
});

// 아이디 중복체크 메서드
async function usernameCheck() {

    let username = $("#username").val();

    let response = await fetch(`/api/user/username-same-check?username=${username}`); // GET 요청
    let responseParse = await response.json(); // 통신에 실패해도 에러메세지를 위해 파싱 해야함

    // console.log(response); // 통신에 성공했니? 정보를 가지고있음
    // response.status 값이 200일때만 파싱하면됨
    // console.log(responseParse); // body 값만 가지고있음

    if (response.status === 200) { // 통신성공
        if (!responseParse) {
            valid.username.state = false;
            valid.username.msg = "중복된 유저네임입니다.";
        } else {
            valid.username.state = true;
            valid.username.msg = "";
        }
    } else { // 통신실패 -> GlobalExceptionHandler로 넘어감 -> e.getMessage가 responseParse에 담김
        alert(responseParse);
    }
}

// 비밀번호 동일체크 메서드
function passwordSameCheck() {
    // 1. password 값 찾기
    let password = $("#password").val();

    // 2. password-check 값 찾기
    let passwordCheck = $("#password-check").val();

    // 3. 두 개 값 비교하기
    if (password === passwordCheck) {
        valid.password.state = true;
        valid.password.msg = "";
    } else {
        valid.password.state = false;
        valid.password.msg = "패스워드가 동일하지 않습니다.";
    }
}

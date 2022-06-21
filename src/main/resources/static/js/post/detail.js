// 게시글 좋아요 체크
function postLikeClick(postId) {

    let principalId = $("#principal-id").val();

    if (principalId === undefined) {
        alert("로그인이 필요합니다.");
        location.href = "/login-form";
        return; // 안붙여주면 아래 코드까지 실행됨
    }

    let isLike = $(`#my-heart`).hasClass("my_fake_like");

    if (isLike) {
        postUnLike(postId);
    } else {
        postLike(postId);
    }
}

async function postLike(postId) {

    let response = await fetch(`/s/api/post/${postId}/love`, {
        method: 'POST'
    });

    let responseParse = await response.json();

    if (response.status == 201) {
        $(`#my-heart`).addClass("my_fake_like");
        $(`#my-heart`).removeClass("my_fake_un_like");
        $(`#my-heart`).removeClass("fa-regular");
        $(`#my-heart`).addClass("fa-solid");

        $("#my-loveId").val(responseParse.loveId);
    } else {
        alert("통신실패");
    }
}

async function postUnLike(postId) {

    let loveId = $("#my-loveId").val();

    let response = await fetch(`/s/api/post/${postId}/love/${loveId}`, {
        method: 'DELETE'
    });

    if (response.status == 200) {
        $(`#my-heart`).addClass("my_fake_un_like");
        $(`#my-heart`).removeClass("my_fake_like");
        $(`#my-heart`).removeClass("fa-solid");
        $(`#my-heart`).addClass("fa-regular");
    } else {
        alert("통신실패");
    }
}

// 게시글 삭제, 권한체크 후 삭제 /s/api/post/postId
$("#btn-delete").click(() => {
    postDelete();
});

let postDelete = async () => {

    let postId = $("#postId").val();
    let pageOwnerId = $("#pageOwnerId").val();

    let response = await fetch(`/s/api/post/${postId}`, {
        method: "DELETE"
    });

    if (response.status == 200) {
        location.href = `/user/${pageOwnerId}/post`;
    } else {
        alert("삭제실패");
    }
};
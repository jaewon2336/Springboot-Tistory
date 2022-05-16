// 게시글 좋아요 체크
function postLikeClick(id) { // postId
    let isLike = $(`#heart-${id}`).hasClass("my_fake_like");

    if (isLike) {
        postUnLike(id);
    } else {
        postLike(id);
    }
}

async function postLike(id) {

    // fetch();
    let response = await fetch(`/s/api/post/${id}/love`, {
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        method: 'POST'
    });

    let responseParse = await response.json();

    if (response.status == 201) {
        alert("좋아요!");

        $(`#heart-${id}`).addClass("my_fake_like");
        $(`#heart-${id}`).removeClass("my_fake_un_like");
        $(`#heart-${id}`).removeClass("fa-regular");
        $(`#heart-${id}`).addClass("fa-solid");

        $("#loveId").val(responseParse.loveId);
    } else {
        alert("통신실패");
    }
}

async function postUnLike(id) {

    let loveId = $("#loveId").val();

    // fetch();
    let response = await fetch(`/s/api/post/${id}/love/${loveId}`, {
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        method: 'DELETE'
    });

    if (response.status == 200) {
        alert("좋아요 취소!");
        $(`#heart-${id}`).addClass("my_fake_un_like");
        $(`#heart-${id}`).removeClass("my_fake_like");
        $(`#heart-${id}`).removeClass("fa-solid");
        $(`#heart-${id}`).addClass("fa-regular");
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
        alert("삭제성공");
        location.href = `/user/${pageOwnerId}/post`;
    } else {
        alert("삭제실패");
    }
};
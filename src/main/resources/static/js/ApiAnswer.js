
function appendAnswer({id, contents, question, writer, formattedCreateDate}) {
    const html = `
        <article class="article">
            <div class="article-header">
                <div class="article-header-thumb">
                    <img src="https://graph.facebook.com/v2.3/1324855987/picture" class="article-author-thumb" alt="">
                </div>
                <div class="article-header-text">
                    <a href="#" class="article-author-name">${writer.name}</a>
                    <div class="article-header-time">${formattedCreateDate}</div>
                </div>
            </div>
            <div class="article-doc comment-doc">
                ${contents}
            </div>
            <div class="article-util">
            <ul class="article-util-list">
                <li>
                    <a class="link-modify-article" href="/api/questions/${question.id}/answers/${id}">수정</a>
                </li>
                <li>
                    <form class="delete-answer-form" action="/api/questions/${question.id}/answers/${id}" method="POST">
                        <input type="hidden" name="_method" value="DELETE">
                        <button type="submit" class="delete-answer-button">삭제</button>
                    </form>
                </li>
            </ul>
            </div>
        </article> `

    $(".qna-comment-slipp-articles").insertAdjacentHTML("beforeend", html);
}

function $(selector) {
    return document.querySelector(selector);
}

document.addEventListener("DOMContentLoaded", () => {
    initEvents();
})

function initEvents() {
    const answerBtn = $(".submit-write .btn");
    if(answerBtn === null) return;
    answerBtn.addEventListener("click", registerAnswerHandler);
}

function fetchManager({ url, method, body, headers, callback }) {
    fetch(url, {method,body,headers,credentials: "same-origin"})
        .then((response) => {
        return response.json()
    }).then((result) => {
        callback(result)
    })
}

function registerAnswerHandler(evt) {
    evt.preventDefault();
    const contents = $(".submit-write textarea").value;
    $(".form-control").value = "";

    fetchManager({
        url: '/api/questions/1/answers',
        method: 'POST',
        headers: { 'content-type': 'application/json'},
        body: JSON.stringify({contents}),
        callback: appendAnswer
    })
}




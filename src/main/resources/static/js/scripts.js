String.prototype.format = function() {
  var args = arguments;
  return this.replace(/{(\d+)}/g, function(match, number) {
    return typeof args[number] != 'undefined'
        ? args[number]
        : match
        ;
  });
};

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

    const answersParent = $(".qna-comment-slipp-articles");
    answersParent.addEventListener("click", removeAnswerHandler);
}

function fetchManager({url, method, body, headers, callback}) {
    fetch(url, {method, body, headers, credentials: "same-origin"})
        .then((response) => {
        return response.json()
    }).then((result) => {
        callback(result)
    })
}

function registerAnswerHandler(evt) {
    evt.preventDefault();
    const contents = $(".submit-write textarea").value;
    $(".submit-write textarea").value = "";

    console.log('/api/questions/' + evt.target.getAttribute('data-question-id') + '/answers');

    fetchManager({
        url:'/api/questions/' + evt.target.getAttribute('data-question-id') + '/answers',
        method:'POST',
        headers: {'content-type': 'application/json'},
        body: JSON.stringify({contents}),
        callback: appendAnswer
    })
}

function removeAnswerHandler(evt) {
    evt.preventDefault();
    if (evt.target.className != "delete-answer-button")
        return;

    fetchManager({
        url:'/api/questions/' + evt.target.getAttribute('data-question-id') +
            '/answers/' + evt.target.getAttribute('data-answer-id'),
        method:'DELETE',
        headers: {'content-type': 'application/json'},
        callback: removeAnswer
    })
}

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
                    <a class="link-modify-article" href="/#">수정</a>
                </li>
                <li>
                    <input type="hidden" name="_method" value="DELETE">
                    <button type="submit" class="delete-answer-button" data-answer-id="${id}" data-question-id="${question.id}">삭제</button>
                </li>
            </ul>
            </div>
        </article> `

    $(".qna-comment-slipp-articles").insertAdjacentHTML("beforeend", html);
}

function removeAnswer({id, contents, question, writer, formattedCreateDate, deleted}) {
    if(!deleted)
        return;
    const answerId = '[data-answer-id="' + id + '"]';
    const deleteBtn = $(answerId);
    $(".qna-comment-slipp-articles").removeChild(findParentArticle(deleteBtn));
}

function findParentArticle(element) {
    if(element.tagName.toLowerCase() == "article")
        return element;
    return findParentArticle(element.parentElement);
}
document.addEventListener("DOMContentLoaded", () => {
    enableEasyMDE();
    copyTextareaValidationError();
});

function enableEasyMDE() {
    new EasyMDE({
        hideIcons: ["fullscreen", "guide"],
        indentWithTabs: false,
        insertTexts: {
            image: ["![](https://", ")"]
        },
        minHeight: "40vh",
        placeholder: "",
        previewRender: (plainText, preview) => {
            if (!plainText.trim()) return "";

            fetch(RENDER_MARKDOWN_URL, {
                method: "POST",
                body: plainText
            })
            .then(res => res.text())
            .then(html => preview.innerHTML = html)
            .then(() => preview.querySelectorAll("pre code").forEach(hljs.highlightBlock));

            return "載入中...";
        },
        uploadImage: true,
        imageUploadEndpoint: UPLOAD_IMAGE_URL,
        imageTexts: {
            sbInit: "圖片可以透過拖曳或剪貼簿上傳",
            sbOnDragEnter: "放開以上傳圖片",
            sbOnDrop: "上傳#images_names#",
            sbProgress: "上傳中#progress#%",
            sbOnUploaded: "上傳成功"
        },
        renderingConfig: {
            codeSyntaxHighlighting: true
        },
        spellChecker: false,
        showIcons: ["code", "table"],
        sideBySideFullscreen: false,
        tabSize: 4,
        status: ["content-error", "upload-image"],
        toolbarTips: false
    });
}

function copyTextareaValidationError() {
    let errorContainer = document.getElementById("contentError");
    document.querySelector(".content-error").textContent = errorContainer ? errorContainer.textContent : "";
}
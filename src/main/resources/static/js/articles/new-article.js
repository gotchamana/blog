document.addEventListener("DOMContentLoaded", () => {
    enableEasyMDE();
    copyTextareaValidationError();

    document.getElementById("coverPictureBinary").onchange = function () {
        if (this.files && this.files[0]) {
            document.getElementById("selectedPictureName").value = this.files[0].name;
            let reader = new FileReader();
            // reader.onload = () => document.getElementById("coverPicture").value = reader.result;
            reader.onload = () => console.log(reader.result);
            reader.onerror = console.log;
            reader.readAsDataURL(this.files[0]);
        } else {
            document.getElementById("selectedPictureName").value = "";
            document.getElementById("coverPicture").value = "";
        }
    };

});

function enableEasyMDE() {
    new EasyMDE({
        autosave: {
            enabled: false,
            uniqueId: "1"
        },
        hideIcons: ["fullscreen", "guide"],
        indentWithTabs: false,
        insertTexts: {
            image: ["![", "](https://)"]
        },
        minHeight: "40vh",
        placeholder: "",
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

function clearSelectedPicture() {
    document.getElementById("coverPictureBinary").value = "";
    document.getElementById("coverPicture").value = "";
    document.getElementById("selectedPictureName").value = "";
    this.blur();
}

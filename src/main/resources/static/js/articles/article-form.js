class Tag {

    /**
     * 
     * @param {string} text 
     * @param {boolean} closeable 
     */
    constructor(text = "", closeable = false) {
        this.text = text;
        this.closeable = closeable;
        this.element = this._createTagElement(text, closeable);
    }

    /**
     * @private
     */
    _createTagElement(text, closeable) {
        let element = document.createElement("div");
        element.classList.add("chip", "waves-effect");
        element.textContent = text;

        if (closeable) {
            let icon = this._createCloseIcon();
            icon.addEventListener("click", () => element.remove());
            element.appendChild(icon);
        }

        return element;
    }

    /**
     * @private
     */
    _createCloseIcon() {
        let icon = document.createElement("i");
        icon.classList.add("close", "fas", "fa-times");
        return icon;
    }

    /**
     * @param {(t: Tag) => void} action
     */
    set onClose(action) {
        let icon = this.element.querySelector("i");

        if (icon)
            icon.addEventListener("click", () => action(this));
    }
}

document.addEventListener("DOMContentLoaded", () => {
    addExistTags();
    enableEasyMDE();
    copyTextareaValidationError();
    enableAutoComplete();
});

function addExistTags() {
    EXIST_TAGS.forEach(addTag);
}

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
            .then(() => {
                preview.querySelectorAll("pre code").forEach(hljs.highlightBlock);
                preview.querySelectorAll("img").forEach(img => img.classList.add("img-fluid"));
            });

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

function enableAutoComplete() {
    new autoComplete({
        data: {
            src: fetch(GET_ALL_TAGS_URL).then(res => res.json()),
            cache: false
        },
        selector: "#tags",
        highlight: true,
        onSelection: feedback => {
            document.getElementById("tags").value = feedback.selection.value;
        }
    });
}

function preventFormSubmit(e) {
    if (e.key === "Enter") e.preventDefault();
}

function addTagButtonOnClick() {
    addTag(getInputTagText());
    clearInputTagText();
}

function getInputTagText() {
    return document.getElementById("tags").value.trim();
}

function addTag(text) {
    if (!text) return;

    let container = getTagContainer();
    if (container.containsTagText(text)) return;

    let input = document.createElement("input");
    input.type = "hidden";
    input.name = "tags";
    input.value = text;

    let tag = new Tag(text, true);
    tag.onClose = () => input.remove();

    container.append(tag.element, input);
}

function getTagContainer() {
    let container = document.getElementById("tagContainer");

    container.containsTagText = text =>
        Array.from(container.querySelectorAll("input[type=hidden]"))
            .map(e => e.value)
            .includes(text);

    return container;
}

function clearInputTagText() {
    let input = document.getElementById("tags");
    input.value = "";
    input.dispatchEvent(new Event("blur"));
}
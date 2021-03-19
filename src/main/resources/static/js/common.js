document.addEventListener("DOMContentLoaded", () => {
    enableNavbarToggler();
});

function enableNavbarToggler() {
    document.querySelector(".navbar-toggler")
        .addEventListener("click", () => document.querySelector(".hamburger-icon").classList.toggle("open"));
}

function onSearchFormSubmit(event) {
    const query = this.querySelector("input").value;

    if (query == null || query.trim().length == 0)
        event.preventDefault();
}
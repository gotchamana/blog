document.addEventListener("DOMContentLoaded", () => {
    enableNavbarToggler();
});

function enableNavbarToggler() {
    document.querySelector(".navbar-toggler")
        .addEventListener("click", () => document.querySelector(".hamburger-icon").classList.toggle("open"));
}

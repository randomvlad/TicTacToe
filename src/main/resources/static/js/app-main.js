function ready(callFunctionOnReady) {
  if (document.readyState !== 'loading') {
    callFunctionOnReady();
  } else {
    document.addEventListener('DOMContentLoaded', callFunctionOnReady);
  }
}

function addClickEvent(elementId, clickFunction) {
  const element = document.getElementById(elementId);
  element.addEventListener("click", clickFunction);
}

ready(() => {

  const gameOverState = document.getElementById("is-game-over").value;

  if (gameOverState !== "true") {
    const availableTiles = document.querySelectorAll(".board-row-tile.available");

    availableTiles.forEach(tile => {
      tile.addEventListener("click", (event) => {
        document.getElementById("tile-id").value = event.target.id;
        document.getElementById("form-mark-tile").submit();
      });
    });
  }

  addClickEvent("btn-new-game", () => {
    document.getElementById("new-game").value = "yes";
    document.getElementById("form-mark-tile").submit();
  });

  addClickEvent("link-user-logout", () => {
    document.getElementById("form-logout").submit();
  });
});

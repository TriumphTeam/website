const elementId = "version-select"
const clickElementId = "version-select-button"

const clickElement = document.getElementById(clickElementId)

clickElement.addEventListener("click", function (event) {
    const targetElement = document.getElementById(elementId)
    const classes = targetElement.classList
    // Only show if it is hidden
    if (classes.contains("hidden")) {
        classes.remove("hidden")
        return
    }

    // Don't hide if clicking on the links area
    if (targetElement.contains(event.target)) return

    // Hide links
    classes.add("hidden")
});

document.addEventListener("click", function (event) {
    // Don't do global hide if clicking on the version element
    if (clickElement.contains(event.target)) return

    const element = document.getElementById(elementId)

    // Hide if it's not hidden
    const classes = element.classList
    if (!classes.contains("hidden")) {
        classes.add("hidden")
    }
});

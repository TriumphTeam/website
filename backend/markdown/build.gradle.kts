
dependencies {
    val commonmark = "0.18.0"

    // MD things
    api("org.commonmark:commonmark:$commonmark")
    api("org.commonmark:commonmark-ext-autolink:$commonmark")
    api("org.commonmark:commonmark-ext-gfm-strikethrough:$commonmark")
    api("org.commonmark:commonmark-ext-gfm-tables:$commonmark")
    api("org.commonmark:commonmark-ext-task-list-items:$commonmark")
}
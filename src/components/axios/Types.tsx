/**
 * Entry type for easy mapping the summary API call
 */
export type Entry =
    | { type: "HEADER", literal: string }
    | { type: "LINK", literal: string, destination: string }
    | { type: "MENU", main: Entry, children: Entry[] }
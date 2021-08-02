/**
 * Entry type for easy mapping the summary API call
 */
export type Entry =
    | { type: "HEADER", literal: string, }
    | { type: "LINK", literal: string, destination: string, indent: number, }

export const SideBarSize = 300

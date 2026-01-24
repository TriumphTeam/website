import {defineConfig} from "vite"
import tailwindcss from "@tailwindcss/vite"

export default defineConfig({
    root: "kotlin",
    plugins: [tailwindcss()]
})

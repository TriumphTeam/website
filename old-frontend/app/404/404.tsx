import type {Route} from "./+types/home"
import {Welcome} from "~/welcome/welcome"

export function meta({}: Route.MetaArgs) {
  return [
    {title: "404 - Not Found"},
    {name: "description", content: "This page could not be found."},
  ]
}

export default function Home() {
  return <div className="w-screen h-screen flex items-center justify-center bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]">
      <div className="text-9xl font-bold">404</div>
  </div>
}

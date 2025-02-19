import {Welcome} from "~/welcome/welcome"
import type {Route} from "../+types/home"
import {redirect} from "react-router"
import {useEffect} from "react"
import {Sidebar} from "~/docs/sidebar"
import {Navbar} from "~/docs/navbar"
import {Content} from "~/docs/content"

// provides `loaderData` to the component
export async function loader({params}: Route.LoaderArgs) {
  let requestUrl = createRequestUrl(params.versionProject, params.projectPage, params.page)
  if (!requestUrl) return redirect("/404")
  return {name: requestUrl}
}

export default function Home({loaderData}: Route.ComponentProps) {
  const {name} = loaderData

  return <>
    <Sidebar/>
    <Navbar/>
    <Content/>
  </>
}

function createRequestUrl(version: string | undefined, project: string | undefined, page: string | undefined) {
  let url = ""
  if (version) url += version + "/"
  if (project) url += project + "/"
  if (page) url += page + "/"
  if (url === "") return undefined
  return url
}
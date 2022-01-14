import React, {useEffect, useState} from "react"
import {Redirect, useParams} from "react-router-dom"
import useSWR from "swr"
import {Box} from "@mui/material"

import Prism from "prismjs"
import "prismjs/components/prism-java"
import "../../prism/prism-kotlin-custom"
import "../../prism/prism-groovy-custom"
import "../../prism/theme/one-dark.css"

import {createToast} from "vercel-toast"
import "../toast.css"
import "./content.scss"

// Import DOMPurify
// TODO think if should really use or not
//const DOMPurify = require("dompurify")(window)

export const WikiContent: React.FC<{ url: string }> = ({url}) => {

  const [registered, setRegistered] = useState(false)

  // Simply for getting the current path url
  const {type, project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API data
  const {data, error} = useSWR(`project/${type}/page/${project}/${page}`)

  // Sets up all code highlighting
  useEffect(() => {
    Prism.highlightAll()
  })

  useEffect(() => {
    // This component is being mounted twice for some reason,
    // so this registration is used to make sure the listener only runs once
    // TODO try to fix this lmao
    if (registered) return
    setRegistered(true)

    document.addEventListener("click", (event) => {
      const target = event.target || event.srcElement
      if (target == null) return
      if (!(target instanceof Element)) return
      const element = target as Element
      if (element.id !== "copy") return
      console.log("clicked?")
      const parent = element.parentElement
      if (parent == null) return
      if (!(parent instanceof HTMLPreElement)) return
      const content = parent.textContent
      if (content == null) return

      navigator.clipboard.writeText(content).then(() => {
        createToast("Code copied to clipboard!", {
          timeout: 2000,
          type: "dark",
        })
      })
    })

  }, [registered])

  // Redirects to introduction if no page is typed
  if (page == null) return <Redirect to={`${url}/introduction`}/>

  // TODO right now this will redirect on any error, might wanna change to only 404 or something
  if (error) return <Redirect to="/404"/>

  // Sets the content
  return <>
    <Box
        sx={{
          color: (theme) => theme.palette.text.secondary,
          "& a": {
            color: (theme) => theme.palette.primary.main,
          },
          "& h1": {
            color: (theme) => theme.palette.text.primary,
          },
          "& h2": {
            fontSize: "1.6em",
            color: (theme) => theme.palette.text.primary,
          },
          "& h3": {
            fontSize: "1.4em",
            color: (theme) => theme.palette.text.primary,
          },
        }}
        id="page"
        className="wiki-content"
        dangerouslySetInnerHTML={{__html: data}}
    />
  </>
}

/**
 * dangerouslySetInnerHTML=
 {
    {
      __html: data
    }
  }
 */

export default WikiContent

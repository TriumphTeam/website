import React, {useEffect, useState} from "react"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {Redirect, useParams} from "react-router-dom"
import useSWR from "swr"

import Prism from "prismjs"
import "prismjs/components/prism-java"
import "../prism/prism-kotlin-custom"
import "../prism/prism-groovy-custom"
import "../prism/theme/one-dark.css"

import {createToast} from "vercel-toast"
import "./toast.css"

// Import DOMPurify
const DOMPurify = require("dompurify")(window)

export const WikiContent: React.FC<{ url: string }> = ({url}) => {
  // CSS styles
  const classes = useStyles()

  const [registered, setRegistered] = useState(false)

  // Simply for getting the current path url
  const {project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API data
  const {data, error} = useSWR(`/page/${project}/${page}`)

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
    <div id="page" className={classes.wikiContent} dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(data)}}/>
  </>
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      wikiContent: {
        width: "70%",
        marginLeft: "auto",
        marginRight: "auto",
        padding: "0 10px",
        fontSize: "1.2em",
        "& img": {
          maxWidth: "100%",
        },
        "& a": {
          color: theme.palette.primary.main,
        },
        "& h2": {
          fontSize: "2em",
        },
        "& h3": {
          fontSize: "1.5em",
        },
        "& #header:hover": {
          "& #hash": {
            opacity: .3,
          },
        },
        "& #hash": {
          position: "absolute",
          paddingRight: "12px",
          paddingLeft: "6px",
          fontWeight: 500,
          opacity: 0,
          textAlign: "right",
          "-webkit-transform": "translateX(-100%)",
          transform: "translateX(-100%)",
          "-webkit-transition": "opacity .1s,color .1s",
          transition: "opacity .1s,color .1s",
          color: theme.palette.text.primary,
        },
        "& #code": {
          position: "relative",
        },
        "& #code:hover": {
          "& #copy": {
            opacity: .2,
          },
        },
        "& #copy": {
          position: "absolute",
          right: 0,
          "-webkit-transform": "translateX(-150%) translateY(-15%)",
          transform: "translateX(-150%) translateY(-15%)",
          opacity: 0,
          color: theme.palette.text.primary,
          cursor: "pointer",
        },
      },
    }),
)

export default WikiContent

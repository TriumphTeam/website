import React, {useEffect} from "react"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {Redirect, useParams} from "react-router-dom"
import useSWR from "swr"
import Prism from "prismjs"
import "prismjs/components/prism-java"
import "prismjs/components/prism-kotlin-custom"
import "prismjs/components/prism-groovy-custom"
import "./page.css"

// Import DOMPurify
const DOMPurify = require("dompurify")(window)

export const WikiContent: React.FC<{ url: string }> = ({url}) => {
  // CSS styles
  const classes = useStyles()

  // Simply for getting the current path url
  const {project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API data
  const {data, error} = useSWR(`/page/${project}/${page}`)

  // Sets up all code highlighting
  useEffect(() => {
    Prism.highlightAll()
  })

  // Redirects to introduction if no page is typed
  if (page == null) return <Redirect to={`${url}/introduction`}/>

  // TODO right now this will redirect on any error, might wanna change to only 404 or something
  if (error) return <Redirect to="/404"/>

  // Sets the content
  return <div className={classes.wikiContent} dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(data)}}/>
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      wikiContent: {
        width: "90%",
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
        "& h1": {
          fontSize: "2em",
        },
        "& h2": {
          fontSize: "1.5em",
        },
      },
    }),
)

export default WikiContent

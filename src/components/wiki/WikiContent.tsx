import React from "react"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {Redirect, useParams} from "react-router-dom"
import useSWR from "swr"

// Import DOMPurify
const DOMPurify = require("dompurify")(window)

export const WikiContent: React.FC<{ url: string }> = ({url}) => {
  // CSS styles
  const classes = useStyles()

  // Simply for getting the current path url
  const {project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API data
  const {data, error} = useSWR(`/page/${project}/${page}`)

  // Redirects to introduction if no page is typed
  if (page == null) return <Redirect to={`${url}/introduction`}/>

  // TODO right now this will redirect on any error, might wanna change to only 404 or something
  if (error) return <Redirect to="/404"/>

  // Sets the content
  return <div dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(data)}}/>
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
    }),
)

export default WikiContent

import React from "react"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {Redirect, useParams} from "react-router-dom"
import useSWR from "swr"

// Import DOMPurify
const DOMPurify = require("dompurify")(window)

type ContentEntry = { literal: string, indent: number }
type ContentData = { link: string, entries: ContentEntry[] }

export const TableOfContents: React.FC<{ url: string }> = ({url}) => {
  // CSS styles
  const classes = useStyles()

  // Simply for getting the current path url
  const {project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API data
  const {data, error} = useSWR<ContentData>(`/content/${project}/${page}`)

  // Redirects to introduction if no page is typed
  if (page == null) return <Redirect to={`${url}/introduction`}/>

  // TODO right now this will redirect on any error, might wanna change to only 404 or something
  if (error) return <Redirect to="/404"/>

  const indentation = (indent: number) => {
    switch (indent) {
      case 1:
        return <span className={classes.indent1}/>
      case 2:
        return <span className={classes.indent2}/>
      default:
        return <></>
    }
  }

  // <div className={classes.editOn}><i className="fab fa-github-square"/> Edit on GitHub</div>
  // Sets the table of contents
  return (
      <div className={classes.tableOfContent}>
        <div className={classes.contentTitle}>On this page</div>
        {
          data?.entries?.map(entry => {
            return <p>{indentation(entry.indent)}{entry.literal}</p>
          })
        }
      </div>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    // Pretty dumb solution for the indent but i'll think of something later
    createStyles({
      tableOfContent: {
        position: "fixed",
        padding: "35px 5px",
      },
      contentTitle: {
        fontSize: "1.2em",
        fontWeight: "bold",
      },
      editOn: {
        fontSize: "1.2em",
        color: "#FFFFFFB3",
      },
      indent1: {
        marginRight: "10px",
      },
      indent2: {
        marginRight: "20px",
      },
    }),
)

export default TableOfContents

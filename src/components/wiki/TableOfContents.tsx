import React, {useEffect, useRef, useState} from "react"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {Link, Redirect, useParams} from "react-router-dom"
import useSWR from "swr"

type ContentEntry = { literal: string, indent: number }
type ContentData = { link: string, entries: ContentEntry[] }

export const TableOfContents: React.FC<{ url: string }> = ({url}) => {
  // CSS styles
  const classes = useStyles()

  // Simply for getting the current path url
  const {project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API data
  const {data, error} = useSWR<ContentData>(`/content/${project}/${page}`)

  const [isIntersecting, setIntersecting] = useState(false)

  const observer = new IntersectionObserver(
      ([entry]) => setIntersecting(entry.isIntersecting)
  )

  const test = useRef()

  // Redirects to introduction if no page is typed
  if (page == null) return <Redirect to={`${url}/introduction`}/>

  // TODO right now this will redirect on any error, might wanna change to only 404 or something
  if (error) return <Redirect to="/404"/>

  const indentation = (indent: number) => {
    switch (indent) {
      case 1:
        return classes.level1
      case 2:
        return classes.level2
      default:
        return ""
    }
  }

  // <div className={classes.editOn}><i className="fab fa-github-square"/> Edit on GitHub</div>
  // Sets the table of contents
  return (
      <div className={classes.tableOfContent}>
        <div className={classes.contentTitle}>On this page</div>
        <ul className={classes.contentItems}>
          {
            data?.entries?.map((entry, index) => {
              return <li
                  key={`${entry.literal}-${index}`}
                  className={indentation(entry.indent)}
              >
                <Link to="">{entry.literal}</Link>
              </li>
            })
          }
        </ul>
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
        fontSize: "1.3em",
        fontWeight: "bold",
        marginTop: 0,
        marginBottom: "10px",
      },
      editOn: {
        fontSize: "1.2em",
        color: "#FFFFFFB3",
      },
      contentItems: {
        padding: 0,
        margin: 0,
        listStyle: "none",
        display: "block",
        "& li": {
          display: "block",
          paddingTop: "5px",
          paddingBottom: "5px",
          fontSize: "1.1em",
          lineHeight: 1.5,
          "-webkit-transition": "opacity .1s,color .1s",
          transition: "opacity .1s,color .1s",
        },
        "& a": {
          color: "#FFFFFFB3",
          "-webkit-transition": "opacity .1s,color .1s",
          transition: "opacity .1s,color .1s",
        },
        "& a:hover": {
          color: theme.palette.primary.main,
        },
      },
      level1: {
        paddingLeft: ".8em",
      },
      level2: {
        paddingLeft: "1.6em",
      },
    }),
)

export default TableOfContents

import React, {useEffect} from "react"
import {createStyles, makeStyles} from "@mui/styles"
import {Theme} from "@mui/material/styles"
import {Redirect, useParams} from "react-router-dom"
import {HashLink} from "react-router-hash-link"
import useSWR from "swr"

type ContentEntry = { literal: string, href: string, indent: number }
type ContentData = { link: string, entries: ContentEntry[] }

export const TableOfContents: React.FC<{ url: string }> = ({url}) => {
  // CSS styles
  const classes = useStyles()

  // Simply for getting the current path url
  const {type, project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API data
  const {data, error} = useSWR<ContentData>(`${type}/content/${project}/${page}`)

  useEffect(() => {
    const observer = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        const id = entry.target.getAttribute("id")
        if (entry.isIntersecting) {
          const element = document.querySelector(`#table-content a[id="${id}"]`)
          if (element != null) {
            console.log(classes.active)
            element.classList.add(classes.active)
          }
        } else {
          const element = document.querySelector(`#table-content a[id="${id}"]`)
          if (element != null) {
            element.classList.remove(classes.active)
          }
        }
      })
    }, {threshold: 0.75, rootMargin: "-100px"})
    // Track all sections that have an `id` applied
    document.querySelectorAll("section[id]").forEach((section) => {
      observer.observe(section)
    })
  })

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
        <ul id="table-content" className={classes.contentItems}>
          {
            data?.entries?.map((entry, index) => {
              return <li
                  key={`${entry.literal}-${index}`}
                  className={indentation(entry.indent)}
              >
                <HashLink id={entry.href} to={`#${entry.href}`}>{entry.literal}</HashLink>
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
      active: {
        color: `${theme.palette.primary.main} !important`,
        fontWeight: "bold",
      },
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

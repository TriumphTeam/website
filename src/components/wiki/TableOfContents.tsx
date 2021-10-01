import React, {useEffect} from "react"
import {Redirect, useParams} from "react-router-dom"
import {HashLink} from "react-router-hash-link"
import useSWR from "swr"
import {Box} from "@mui/material"

type ContentEntry = { literal: string, href: string, indent: number }
type ContentData = { link: string, entries: ContentEntry[] }

export const TableOfContents: React.FC<{ url: string }> = ({url}) => {

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
            element.classList.add("active")
          }
        } else {
          const element = document.querySelector(`#table-content a[id="${id}"]`)
          if (element != null) {
            element.classList.remove("active")
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
        return "level-1"
      case 2:
        return "level-2"
      default:
        return ""
    }
  }

  // <div className={classes.editOn}><i className="fab fa-github-square"/> Edit on GitHub</div>
  // Sets the table of contents
  return (
      <Box
          className="table-of-content"
          sx={{
            "& a": {
              color: (theme) => theme.palette.text.secondary,
              transition: "opacity .1s, color .1s",
            },
            "& a:hover": {
              color: (theme) => theme.palette.primary.main,
            },
          }}
      >
        <div className="on-page">On this page</div>
        <ul id="table-content" className="content-items">
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
      </Box>
  )
}

export default TableOfContents

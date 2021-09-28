import React from "react"
import SideBar from "../components/wiki/SideBar"
import {createStyles, makeStyles} from "@mui/styles"
import {Theme} from "@mui/material/styles"
import {Redirect, useParams} from "react-router-dom"
import WikiBody from "../components/wiki/WikiContent"
import {Grid} from "@mui/material"
import TableOfContents from "../components/wiki/TableOfContents"
import {SideBarSize} from "../components/axios/Types"

export default function Wiki() {
  const classes = useStyles()

  // Url data
  const {type, project} = useParams<{ type?: string, project?: string, optionalPage?: string }>()

  const url = `/${type}/${project}`

  // Guarantees it's a valid type
  if (type !== "library" && type !== "plugin") return <Redirect to="/404"/>

  return (
      <>
        <SideBar url={url}/>
        <main className={classes.content}>
          <Grid container spacing={4}>
            <Grid item xs={10}>
              <WikiBody url={url}/>
            </Grid>
            <Grid item xs={2}>
              <TableOfContents url={url}/>
            </Grid>
          </Grid>
        </main>
      </>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      root: {
        display: "flex",
      },
      appBar: {
        zIndex: theme.zIndex.drawer + 1,
      },
      drawerContainer: {
        overflow: "auto",
      },
      content: {
        padding: theme.spacing(3),
        marginLeft: SideBarSize,
      },
      wikiBody: {
        display: "inline-block",
      },
    }),
)

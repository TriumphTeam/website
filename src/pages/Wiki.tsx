import React from "react"
import NavBar from "../components/navigation/NavBar"
import SideBar from "../components/sidebar/SideBar"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import Toolbar from "@material-ui/core/Toolbar"
import {Redirect, useParams} from "react-router-dom"
import WikiBody from "../components/wiki/WikiContent"
import {Grid} from "@material-ui/core"
import TableOfContents from "../components/wiki/TableOfContents"
import {SideBarSize} from "../components/axios/Types"

export default function Wiki() {
  const classes = useStyles()

  // Url data
  const {type, project} = useParams<{ type?: string, project?: string, optionalPage?: string }>()

  const url = `/${type}/${project}`

  // Guarantees it's a valid type
  if (type !== "lib" && type !== "plugin") return <Redirect to="/404"/>

  return (
      <>
        <NavBar/>
        <SideBar url={url}/>
        <main className={classes.content}>
          <Toolbar/>
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

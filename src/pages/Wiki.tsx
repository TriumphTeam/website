import React, {useEffect, useState} from "react"
import NavBar from "../components/navigation/NavBar"
import SideBar from "../components/sidebar/SideBar"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import Toolbar from "@material-ui/core/Toolbar"
import {useParams} from "react-router-dom"
import {Entry} from "../components/axios/Types"
import api from "../components/axios/Api"
import WikiContent from "../components/wiki/WikiContent"

export default function Wiki() {
  const classes = useStyles()

  const {type, name, optionalPath} = useParams<{ type?: string, name?: string, optionalPath?: string }>()
  const [summary, setSummary] = useState<Entry[]>([])

  const url = `/${type}/${name}`

  useEffect(() => {
    // Guarantees it's a valid type
    if (type !== "lib" && type !== "plugin") window.location.replace("/404")

    api.get<{ entries: Entry[] }>("/summary/" + name)
        .then(response => {
          setSummary(response.data.entries)
        })
        .catch(_ => {
          // TODO this might be temporary as i don't know if there is a better way to do this
          window.location.replace("/404")
        })
  }, [])

  return (
      <>
        <NavBar/>
        <SideBar entries={summary} url={url}/>
        <main className={classes.content}>
          <Toolbar/>
          <WikiContent url={url}/>
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
        flexGrow: 1,
        padding: theme.spacing(3),
        marginLeft: "350px",
      },
    }),
)

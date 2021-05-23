import React, {useEffect, useState} from "react"
import NavBar from "../components/navigation/NavBar"
import SideBar from "../components/sidebar/SideBar"
import {createStyles, Theme, makeStyles} from "@material-ui/core/styles"
import Toolbar from "@material-ui/core/Toolbar"
import {useLocation, Redirect} from "react-router-dom"
import {Entry} from "../components/axios/Types"
import api from "../components/axios/Api"

export default function Wiki() {
  const classes = useStyles()
  const location = useLocation()

  const [summary, setSummary] = useState<Entry[]>([])

  useEffect(() => {
    api.get<{ entries: Entry[] }>("/summary/triumph-gui")
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
        <SideBar entries={summary}/>
        <main className={classes.content}>
          <Toolbar/>
          {JSON.stringify(location)}
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
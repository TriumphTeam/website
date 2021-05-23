import React, {useEffect, useState} from "react"
import NavBar from "../components/navigation/NavBar"
import SideBar from "../components/sidebar/SideBar"
import {createStyles, Theme, makeStyles} from "@material-ui/core/styles"
import Toolbar from "@material-ui/core/Toolbar"
import {useLocation} from "react-router-dom"
import {Entry} from "../components/axios/Types"
import api from "../components/axios/Api"

export default function Wiki() {
  const classes = useStyles()
  const location = useLocation()

  const [summary, setSummary] = useState<Entry[]>([])

  useEffect(() => {
    api.get<{ entries: Entry[] }>("/summary/triumph-gui")
        .then(res => {
          setSummary(res.data.entries)
        })
        .catch(err => {
          console.log(err)
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
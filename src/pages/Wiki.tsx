import React from "react"
import NavBar from "../components/navigation/NavBar"
import SideBar from "../components/sidebar/SideBar"
import {createStyles, Theme, makeStyles} from "@material-ui/core/styles"
import Toolbar from "@material-ui/core/Toolbar"
import ReactMarkdown from "react-markdown"


export default function Wiki() {
  const classes = useStyles()
  const test = () => {
      fetch("../projects/").then(r => console.log(r))
  }
  return (
      <>
        <NavBar/>
        <SideBar/>
        <main className={classes.content}>
          <Toolbar/>
          {test()}
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
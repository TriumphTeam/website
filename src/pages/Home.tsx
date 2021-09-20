import React from "react"
import NavBar from "../components/navigation/NavBar"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"

function Home() {
  const classes = useStyles()

  return (
      <>
        <div className={classes.main}>
          Team
        </div>
      </>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    // Pretty dumb solution for the indent but i'll think of something later
    createStyles({
      main: {

      }
    }),
)

export default Home

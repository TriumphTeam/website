import React from "react"
import {createStyles, makeStyles} from "@mui/styles"
import {Theme} from "@mui/material/styles"

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

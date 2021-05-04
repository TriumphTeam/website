import React from "react"
import ListItemText from "@material-ui/core/ListItemText"
import {createStyles, makeStyles, Theme, withStyles} from "@material-ui/core/styles"

interface BarTextProp {
  text: string
}

export const BarText: React.FC<BarTextProp> = ({text}) => {
  const classes = useStyles()

  return (
      <div className={classes.sideText}>
        <ListItemText className={classes.listText} disableTypography>{text}</ListItemText>
      </div>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      sideText: {
        border: "1px solid transparent",
        margin: 0,
        display: "flex",
        padding: "7px 24px 7px 16px",
        position: "relative",
        alignItems: "center",
        borderRight: 0,
        textDecoration: "none",
        "-webkit-box-align": "center",
      },
      listText: {
        fontWeight: 700,
        lineHeight: 1.2,
        letterSpacing: "1.2px",
      }
    }),
)
import React from "react"
import ListItemText from "@material-ui/core/ListItemText"
import {createStyles, makeStyles, Theme, withStyles} from "@material-ui/core/styles"

type BarTextProp = {
  text: string
}

export const BarText: React.FC<BarTextProp> = ({text}) => {
  const classes = useStyles()

  return (
      <div className={classes.sideText}>
        <ListItemText key={text} className={classes.listText} disableTypography>{text}</ListItemText>
      </div>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      sideText: {
        border: "1px solid transparent",
        margin: 0,
        display: "flex",
        padding: "5px 0",
        position: "relative",
        alignItems: "center",
        borderRight: 0,
        textDecoration: "none",
        "-webkit-box-align": "center",
        fontWeight: "bold",
        fontSize: "1.2em"
      },
      listText: {
        //padding: "7px 24px 7px 16px",
      },
    }),
)

export default BarText
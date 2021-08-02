import React from "react"
import ListItemText from "@material-ui/core/ListItemText"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"

type BarTextProp = {
  text: string,
}

export const BarText: React.FC<BarTextProp> = ({text}) => {
  const classes = useStyles()

  return (
      <div className={classes.sideText}>
        <ListItemText key={text} disableTypography>{text}</ListItemText>
      </div>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      sideText: {
        width: "80%",
        marginLeft: "auto",
        marginRight: "auto",
        border: "1px solid transparent",
        display: "flex",
        padding: "5px 0",
        position: "relative",
        alignItems: "center",
        borderRight: 0,
        textDecoration: "none",
        "-webkit-box-align": "center",
        fontWeight: "bold",
        fontSize: "1.3em",
        marginTop: "15px",
      },
    }),
)

export default BarText

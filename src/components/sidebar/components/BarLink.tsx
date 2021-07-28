import React from "react"
import ListItemText from "@material-ui/core/ListItemText"
import ListItem from "@material-ui/core/ListItem"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {Link} from "react-router-dom"

type BarLinkProp = {
  text: string
  indent: number
  destination: string
  active?: boolean
}

export const BarLink: React.FC<BarLinkProp> = ({text, indent, destination, active}) => {
  const classes = useStyles()

  const Indentation = () => {
    if (indent === 1) {
      return <span className={classes.indent}/>
    }

    return <></>
  }

  const activeClass = (active ? ` ${classes.active}` : "")

  return (
      <Link to={destination} className={classes.link}>
        <Indentation/>
        <div className={`${classes.linkContainer}${activeClass}`}>{text}</div>
      </Link>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      link: {
        width: "80%",
        marginLeft: "auto",
        marginRight: "auto",
        textDecoration: "none",
        display: "box",
      },
      linkContainer: {
        fontSize: "1.15em",
        padding: "5px 0",
        color: "#ffffffb3",
        transition: "color .25s",
        "&:hover": {
          background: "none",
          color: "#2980b9",
        },
      },
      indent: {
        marginRight: "25px",
      },
      active: {
        color: "#2980b9",
      },
    }),
)

export default BarLink
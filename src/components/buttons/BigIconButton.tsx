import {styled} from "@mui/material/styles"
import {IconButton} from "@mui/material"

const BigIconButton = styled(IconButton)({
  width: "75px",
  background: "#1D1D1F",
  borderRadius: "15px",
  padding: "15px",
  fontSize: "2.5em",
  transition: "ease 0.25s",
  "&:hover": {
    transform: "scale(1.1)",
  },
})

export default BigIconButton

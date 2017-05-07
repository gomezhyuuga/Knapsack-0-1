library(readr)

def <- data.frame()

instances <- sprintf("GA-DEFAULT_20_%03d.kp", seq(from = 0, to = 14))

read_csv("GA-DEFAULT_20_000.kp",
                 col_names = c("weight","profit"),
                 skip = 1)
rdcsv <- function(instance) {
  read_csv(instance, col_names = c("weight", "profit"), skip = 1)
}
data <- do.call(rbind, lapply(instances, rdcsv))

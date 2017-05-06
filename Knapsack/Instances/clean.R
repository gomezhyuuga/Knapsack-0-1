library(readr)

default1 <- read_csv("GA-DEFAULT_20_000.kp")
default1[, "weight_sum"] <- sum(default1$`50`)
default1[,"ratio"] <- default1$`20` / default1$`50`
default2 <- read_csv("GA-DEFAULT_20_001.kp")
default3 <- read_csv("GA-DEFAULT_20_002.kp")

maxprofit1 <- read_csv("GA-MAXPROFIT_20_000.kp")
maxprofit2 <- read_csv("GA-MAXPROFIT_20_001.kp")
maxprofit3 <- read_csv("GA-MAXPROFIT_20_002.kp")

minweight1 <- read_csv("GA-MINWEIGHT_20_000.kp")
minweight2 <- read_csv("GA-MINWEIGHT_20_001.kp")
minweight3 <- read_csv("GA-MINWEIGHT_20_002.kp")

profitweight1 <- read_csv("GA-MAXPROFITWEIGHT_20_000.kp")
profitweight2 <- read_csv("GA-MAXPROFITWEIGHT_20_001.kp")
profitweight3 <- read_csv("GA-MAXPROFITWEIGHT_20_002.kp")

default <- rbind(default1, default2, default3)
maxprofit <- rbind(maxprofit1, maxprofit2, maxprofit3)
minweight <- rbind(minweight1, minweight2, minweight3)
profitweight <- rbind(profitweight1, profitweight2, profitweight3)

default[,"class"] <- "DEFAULT"
maxprofit[,"class"] <- "MAXPROFIT"
minweight[,"class"] <- "MINWEIGHT"
profitweight[,"class"] <- "PROFITWEIGHT"

total <- rbind(default, maxprofit, minweight, profitweight)

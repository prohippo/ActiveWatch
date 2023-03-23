// Copyright (c) 2021, C. P. Mah
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//   Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
//
//   Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// -----------------------------------------------------------------------------
// AW file GramMap.java : 21mar2023 CPM
// lookup for built-in alphabetic 4- and 5-grams

package gram;

import java.util.*;

public class GramMap {

	private static final String[] l4g = {
		"aban", "abbl", "abit", "able", "abor", "acce", "acci", "acco", "accr", "acet",
		"ache", "acho", "acid", "acle", "acon", "acqu", "acre", "acro", "adde", "addr",
		"adem", "adet", "adge", "adle", "admi", "ador", "adva", "adve", "aero", "affl",
		"agen", "agge", "aggl", "aggr", "agle", "agon", "agra", "agri", "ague", "aign",
		"aile", "aint", "aire", "aise", "alan", "alco", "alem", "alia", "alle", "allo",
		"ally", "alog", "alom", "alon", "aloo", "alor", "alte", "alth", "alti", "alum",
		"alve", "alyt", "ambe", "ambi", "ambl", "amer", "amin", "amou", "ampl", "anal",
		"anar", "anat", "ance", "anch", "anct", "andi", "andl", "aneu", "ange", "angl",
		"angr", "anim", "anne", "anni", "anod", "anon", "anso", "anta", "ante", "anth",
		"anti", "anyo", "apar", "appe", "appl", "appo", "appr", "apse", "aqua", "arab",
		"arad", "aran", "arbi", "arbo", "arch", "area", "aren", "arge", "argo", "aris",
		"arma", "army", "arne", "arni", "arom", "aron", "arra", "arre", "arri", "arro",
		"arry", "arse", "arte", "arth", "arti", "arva", "arve", "asci", "ashi", "asia",
		"asis", "aske", "ason", "assa", "asse", "assi", "assu", "aste", "asto", "astr",
		"asur", "atar", "atch", "ater", "athe", "athl", "atin", "atio", "atri", "atta",
		"atte", "atti", "attl", "atto", "attr", "atul", "audi", "augh", "ault", "aunt",
		"ause", "aust", "auth", "auto", "avai", "aven", "avid", "awar", "axis", "azzl",
		"babe", "baby", "back", "bact", "bail", "bait", "bake", "bala", "bald", "ball",
		"balm", "band", "bane", "bang", "bank", "bann", "barb", "bard", "bare", "bark",
		"baro", "barr", "base", "bass", "bast", "bate", "bath", "batt", "bble", "bead",
		"bean", "bear", "beat", "beef", "beer", "beli", "bell", "belt", "bend", "bene",
		"bent", "berg", "best", "beve", "bibl", "bide", "bill", "bina", "bind", "bine",
		"bing", "biol", "biom", "bion", "bird", "bite", "bitu", "bjec", "blas", "blea",
		"blem", "blin", "blis", "bloo", "blow", "blue", "boar", "boat", "bode", "body",
		"bola", "bold", "bolt", "bomb", "bond", "bone", "bonk", "book", "boom", "boon",
		"boot", "bore", "born", "boro", "bort", "bott", "boul", "brag", "brai", "bran",
		"brav", "brea", "bred", "brew", "brid", "bris", "brit", "broa", "brow", "buck",
		"buff", "buil", "bulk", "bull", "bump", "bund", "bung", "bunk", "burb", "burg",
		"burn", "burs", "bury", "bush", "bust", "bute", "butt", "byte", "cade", "cafe",
		"cage", "cake", "cala", "calc", "cale", "cali", "call", "calm", "camp", "cana",
		"canc", "cand", "cane", "cann", "cant", "capa", "cape", "capi", "capt", "carb",
		"card", "care", "carn", "carp", "carr", "cart", "case", "cash", "cask", "cass",
		"cast", "cata", "cate", "cath", "catt", "cauc", "caus", "cava", "cave", "ceal",
		"cede", "ceiv", "cele", "cell", "cend", "cens", "cent", "cept", "cere", "cern",
		"cert", "cess", "cest", "chag", "chai", "chal", "cham", "chan", "chap", "char",
		"chas", "chat", "chea", "chem", "chen", "cheo", "chet", "chic", "chie", "chim",
		"chin", "chip", "chis", "chlo", "choo", "chop", "chor", "chow", "chri", "chro",
		"chur", "cial", "cide", "cien", "cile", "cind", "cine", "cise", "cite", "citr",
		"city", "civi", "cket", "ckle", "clad", "clai", "clam", "clan", "clap", "clar",
		"clat", "claw", "cler", "clim", "clin", "clip", "clos", "clot", "club", "coal",
		"coar", "coat", "cock", "coco", "coct", "code", "coff", "cold", "coll", "colo",
		"colt", "colu", "comb", "come", "comm", "comp", "conc", "cond", "cone", "conf",
		"cong", "conn", "cons", "cont", "conv", "cook", "cool", "coon", "coop", "cope",
		"cord", "core", "cork", "corn", "corp", "corr", "cosm", "cost", "cote", "cott",
		"coun", "coup", "cour", "cove", "crab", "cram", "cran", "crap", "cras", "crat",
		"craw", "craz", "crea", "cred", "cree", "crep", "cres", "cret", "crew", "crib",
		"crim", "crip", "cris", "crit", "crop", "cros", "crot", "crow", "cruc", "crue",
		"crui", "crum", "ctic", "ctor", "cuff", "cuit", "cula", "cule", "culp", "cult",
		"cumb", "curb", "curd", "curi", "curr", "curs", "curt", "curv", "cuse", "cush",
		"cuss", "cust", "cute", "cybe", "cyte", "cyto", "dale", "damp", "dana", "danc",
		"dang", "dapt", "dard", "dare", "dark", "dart", "dash", "data", "date", "daug",
		"dawn", "ddle", "dead", "deal", "dean", "dear", "deat", "debi", "debt", "deca",
		"dece", "deci", "decl", "deep", "deer", "defe", "defi", "dela", "dele", "deli",
		"delt", "delu", "deme", "demi", "demn", "demo", "dens", "dent", "deny", "depe",
		"depo", "depr", "dera", "derm", "desc", "desi", "desp", "dest", "dete", "deut",
		"deve", "devi", "dial", "dict", "dida", "dier", "diet", "diff", "digi", "dile",
		"dilu", "dime", "dine", "ding", "dion", "dirt", "disa", "disc", "dise", "dish",
		"disi", "disp", "dist", "dite", "dium", "dive", "divi", "dock", "doct", "dole",
		"doll", "dome", "domi", "dona", "done", "dong", "doom", "door", "dore", "dorm",
		"dors", "dose", "dote", "down", "doze", "drag", "draw", "drea", "dres", "drip",
		"driv", "dron", "drop", "drow", "drug", "drum", "duce", "duck", "dumb", "dump",
		"dunk", "dupl", "dusk", "dust", "dway", "dyna", "dyne", "each", "eage", "eagl",
		"eant", "eard", "earm", "earn", "eart", "ease", "easo", "east", "eate", "eath",
		"eave", "ebat", "ebra", "ebri", "ebut", "ecal", "eche", "echo", "ecia", "ecip",
		"ecla", "ecom", "econ", "ecor", "ecre", "ecto", "ectr", "edal", "eden", "eder",
		"edes", "edge", "edib", "edit", "educ", "eech", "eese", "eeti", "eeze", "efea",
		"effo", "egen", "egim", "egis", "egot", "eigh", "eign", "elan", "eleb", "elec",
		"eleg", "elem", "eles", "elig", "elim", "elin", "elit", "elle", "elli", "elon",
		"elte", "elve", "emai", "eman", "embe", "embl", "embr", "emed", "emem", "emen",
		"emer", "emis", "emol", "emon", "emor", "emot", "empe", "empl", "empt", "enal",
		"enam", "ence", "ench", "enef", "enfo", "enge", "engl", "enim", "enne", "enom",
		"enon", "enor", "enro", "ense", "ensu", "ente", "enti", "entr", "eopl", "epar",
		"epid", "epis", "epor", "epre", "eput", "equa", "eque", "equi", "erag", "eral",
		"erap", "erce", "erch", "eria", "erie", "eril", "erio", "eris", "erit", "erra",
		"erro", "erry", "erse", "erso", "erty", "erua", "erve", "esca", "esce", "esig",
		"esis", "espr", "esse", "esso", "esth", "esti", "estl", "estr", "etai", "etal",
		"etch", "eter", "ethe", "ethi", "etho", "ethy", "etro", "ette", "ettl", "etto",
		"etty", "eval", "evel", "even", "ever", "evic", "evid", "evil", "evol", "eway",
		"exam", "exce", "exis", "exit", "expa", "expe", "expl", "exte", "extr", "face",
		"fact", "fail", "fair", "fake", "fall", "fals", "fame", "fang", "fant", "fare",
		"farm", "fasc", "fast", "fate", "favo", "fear", "feat", "fect", "fede", "feed",
		"feel", "feit", "feli", "fell", "femi", "fend", "fern", "ferr", "fert", "fess",
		"fest", "ffer", "ffic", "ffle", "ffor", "fice", "fici", "fict", "fide", "fiel",
		"figu", "file", "fili", "fill", "film", "fina", "find", "fine", "fini", "fire",
		"firm", "fish", "fiss", "fist", "flag", "flai", "flam", "flan", "flat", "flee",
		"flex", "flim", "flip", "floo", "flop", "flor", "flou", "flow", "flue", "foil",
		"fold", "foli", "folk", "foll", "fond", "food", "fool", "foot", "forc", "ford",
		"fore", "forg", "fork", "form", "fort", "foul", "frag", "frai", "fran", "frat",
		"free", "fres", "fric", "frug", "fter", "fuel", "fuge", "full", "fume", "func",
		"fund", "fung", "furn", "fury", "fuse", "fuss", "gage", "gain", "gale", "gall",
		"gamb", "game", "gang", "gant", "garb", "gard", "gast", "gate", "gath", "gave",
		"gear", "genc", "gend", "gene", "geni", "gent", "genu", "geon", "geri", "germ",
		"gest", "ggle", "gift", "giga", "gile", "gine", "gird", "girl", "give", "glad",
		"glam", "glan", "glen", "glob", "gnal", "goat", "gold", "golf", "gone", "good",
		"goon", "gorg", "gory", "gote", "grad", "gram", "gran", "grap", "grat", "grav",
		"gray", "gree", "gren", "grid", "grin", "gros", "grou", "grow", "grum", "guar",
		"guil", "gula", "gulf", "gull", "gust", "hack", "hail", "hair", "hale", "half",
		"hall", "halo", "hame", "hamp", "hanc", "hand", "hane", "hank", "hant", "happ",
		"hard", "hark", "harm", "harp", "harr", "hart", "hase", "hast", "hate", "have",
		"hawk", "hbor", "head", "heal", "heap", "hear", "heat", "heav", "heel", "heir",
		"held", "heli", "hell", "helm", "help", "hema", "hemi", "hemo", "hend", "hera",
		"herb", "herd", "here", "heri", "hero", "hest", "hexa", "hibi", "hick", "hief",
		"high", "hill", "hind", "hine", "hing", "hint", "hion", "hist", "hive", "hman",
		"hock", "hoke", "hold", "hole", "holo", "holy", "home", "hone", "honk", "hony",
		"hood", "hook", "hool", "hope", "hore", "hori", "horn", "horr", "hose", "host",
		"hour", "hous", "hrop", "hull", "huma", "humb", "hump", "hund", "hunk", "hunt",
		"hurl", "hush", "hust", "hway", "hydr", "hype", "hypn", "hyst", "iber", "ibut",
		"ican", "icat", "icer", "icip", "icle", "icot", "iday", "idea", "idge", "idol",
		"ield", "iend", "ient", "ieve", "iffe", "iffi", "ifle", "igat", "igen", "iger",
		"ight", "igni", "ilet", "ilia", "illa", "ille", "illi", "imag", "imen", "imet",
		"imit", "imme", "immo", "immu", "impa", "impe", "impl", "impo", "impr", "imul",
		"inap", "ince", "inch", "inci", "inco", "inct", "inde", "indi", "indu", "inet",
		"infe", "infi", "infl", "info", "inge", "ingo", "ingu", "inim", "inno", "inse",
		"insp", "inst", "insu", "inta", "inte", "inti", "intr", "inue", "inut", "inve",
		"invo", "ipal", "iple", "ipro", "ipse", "ique", "irch", "iron", "irra", "irre",
		"irri", "isan", "isce", "isci", "isit", "isla", "isle", "ison", "ispo", "issu",
		"iste", "istl", "istr", "ital", "itar", "itch", "iter", "ithe", "ithm", "itio",
		"itis", "iton", "itte", "ittl", "itud", "itut", "ival", "ivat", "ivel", "iver",
		"ivil", "ivor", "izen", "izzl", "jack", "jade", "jail", "jang", "japa", "jazz",
		"jean", "ject", "jerk", "jers", "jest", "jing", "join", "jour", "judi", "jump",
		"juni", "junk", "jure", "jury", "just", "keep", "kick", "kill", "kilo", "kind",
		"kine", "king", "kiss", "kitc", "knee", "knit", "knot", "know", "labo", "lace",
		"lack", "lact", "lade", "lady", "lage", "laid", "laim", "lain", "lamb", "lami",
		"lamp", "land", "lane", "lang", "lank", "lant", "lapi", "lard", "lare", "larg",
		"lari", "lark", "lash", "lass", "last", "late", "lati", "latt", "lava", "lave",
		"lawn", "lead", "lean", "lear", "leas", "leav", "lebr", "lect", "ledg", "leep",
		"left", "lega", "lege", "legi", "lend", "lent", "leon", "lert", "less", "lest",
		"lete", "lett", "leve", "liab", "libe", "libr", "lice", "lici", "lick", "lict",
		"lide", "lief", "lien", "life", "liff", "lift", "lige", "ligh", "ligi", "lign",
		"like", "limb", "lime", "limi", "limp", "lind", "line", "ling", "link", "lint",
		"lion", "liqu", "lish", "list", "lite", "lith", "live", "llab", "llar", "llet",
		"lley", "llop", "llow", "load", "loan", "loat", "lobe", "loca", "lock", "loft",
		"logi", "loin", "lone", "long", "look", "loom", "loon", "loop", "lope", "loqu",
		"lord", "lore", "lorn", "lose", "loss", "loud", "lout", "love", "lste", "lter",
		"luck", "lude", "luff", "lumb", "lume", "lumi", "lump", "luna", "lund", "lung",
		"lunk", "lunt", "lure", "lush", "lust", "lute", "luth", "lway", "mace", "mach",
		"mage", "magi", "maid", "mail", "main", "majo", "make", "mala", "male", "mall",
		"manc", "mand", "mang", "mani", "mank", "mans", "mant", "manu", "mare", "marg",
		"mari", "mark", "marr", "mart", "mary", "masc", "mash", "mask", "mass", "mast",
		"mate", "math", "matr", "matt", "matu", "maze", "mbar", "mbat", "mber", "mbin",
		"mble", "mbra", "meal", "mean", "meas", "meat", "mech", "meda", "medi", "medy",
		"meet", "mega", "mela", "meli", "melo", "melt", "memb", "memo", "mend", "mens",
		"ment", "mera", "merc", "merg", "mess", "mest", "mete", "meth", "metr", "migr",
		"mile", "mili", "milk", "mill", "mina", "mind", "mine", "ming", "mini", "mint",
		"minu", "mira", "mirr", "misc", "mise", "mish", "miss", "mist", "mite", "mmer",
		"mmis", "mmit", "mmon", "mmun", "mock", "mode", "mold", "mona", "mond", "mone",
		"moni", "monk", "mono", "mont", "mony", "moon", "moor", "more", "morn", "mort",
		"mote", "moth", "moti", "moun", "move", "movi", "mpac", "mpar", "mpas", "mpat",
		"mper", "mpet", "mple", "mplo", "mpre", "mula", "mule", "muni", "musc", "muse",
		"mush", "musi", "musk", "must", "mute", "mutu", "myst", "myth", "nack", "nage",
		"nail", "nake", "nald", "name", "nant", "narc", "nate", "nati", "natu", "naut",
		"navy", "ncia", "ncil", "ndam", "nday", "nder", "ndic", "ndit", "ndle", "ndon",
		"neck", "nect", "need", "nega", "neph", "nerg", "nest", "netw", "neut", "news",
		"nfor", "nfra", "ngag", "ngen", "nger", "ngin", "ngle", "ngra", "ngth", "ngue",
		"nice", "nick", "nigh", "nign", "nihi", "nion", "nior", "nito", "nitr", "nkle",
		"nman", "nnel", "nock", "nome", "noon", "norm", "nose", "note", "noti", "noun",
		"nput", "nque", "nsel", "nser", "nset", "nsio", "nsis", "nsit", "nsti", "nstr",
		"nsul", "nsur", "ntag", "ntam", "nten", "nter", "ntim", "ntin", "ntle", "ntra",
		"ntro", "ntry", "numb", "nute", "nway", "oach", "oard", "oast", "oath", "obab",
		"obbe", "ober", "occa", "occu", "ocen", "oche", "ocke", "octa", "octo", "ocus",
		"odel", "odge", "odle", "offe", "offi", "ogra", "oice", "oise", "oist", "olat",
		"ollo", "oman", "omba", "ombi", "omen", "omeo", "omet", "omic", "omma", "ommo",
		"ompa", "ompe", "ompl", "onar", "onat", "onde", "ondo", "ondu", "onen", "oney",
		"onge", "ongr", "onic", "onom", "onor", "onsc", "onst", "ontr", "onym", "oodl",
		"oose", "ooth", "ooze", "open", "opin", "ople", "oppo", "opti", "oral", "orce",
		"orch", "orde", "ordi", "orge", "orid", "orig", "orit", "orna", "orne", "orni",
		"orri", "orth", "ortr", "ortu", "osis", "osit", "ossi", "oste", "otch", "otel",
		"othe", "otog", "ottl", "otto", "oubl", "ouch", "ough", "ould", "ounc", "ound",
		"oung", "ount", "ourn", "ouse", "oust", "oute", "outh", "outl", "oval", "ovel",
		"oven", "over", "ovid", "ower", "oxid", "oyal", "ozzl", "pace", "pack", "pact",
		"page", "paid", "pain", "pair", "pale", "pali", "pall", "palm", "pand", "pane",
		"pani", "pank", "pant", "para", "pard", "pare", "pari", "park", "parl", "part",
		"pass", "past", "pate", "path", "pati", "patr", "patt", "pave", "peak", "peal",
		"pear", "peck", "pect", "pede", "peer", "pell", "pena", "pend", "pene", "peni",
		"pent", "peop", "pera", "perc", "perf", "peri", "perk", "perm", "perp", "pers",
		"pert", "perv", "pess", "pest", "pete", "peti", "petu", "phag", "phan", "phas",
		"phat", "phen", "phil", "phin", "phon", "phor", "phos", "phra", "phyl", "phys",
		"pick", "pict", "pier", "pike", "pile", "pill", "pine", "ping", "pink", "pion",
		"pipe", "pire", "pise", "pist", "plac", "plag", "plan", "plas", "plat", "play",
		"plea", "plet", "plex", "plic", "plot", "ploy", "plum", "plun", "pock", "pode",
		"poin", "pois", "poke", "pole", "poli", "poll", "poly", "pond", "pone", "pong",
		"pont", "pony", "pool", "poon", "poor", "popu", "porc", "pore", "pork", "porn",
		"port", "pose", "posi", "post", "pote", "poti", "pour", "pout", "ppea", "ppen",
		"ppet", "pple", "ppli", "ppor", "pray", "prea", "prec", "pred", "pree", "preg",
		"prem", "prep", "pres", "pret", "prev", "pric", "prim", "prin", "prio", "pris",
		"priv", "prob", "proc", "prof", "prog", "prom", "pron", "prop", "pros", "prot",
		"prov", "prox", "prud", "pter", "ptim", "publ", "pugn", "pula", "pull", "pulm",
		"puls", "pump", "pung", "puni", "punk", "pure", "purg", "purt", "push", "puss",
		"pute", "quad", "qual", "quan", "quar", "quen", "quer", "ques", "quet", "quid",
		"quin", "quir", "quis", "quit", "quiz", "race", "rack", "ract", "rade", "radi",
		"raft", "rage", "raid", "rail", "rain", "rait", "rald", "rama", "ramb", "rame",
		"ramp", "ranc", "rand", "rane", "rang", "rank", "rans", "rant", "rape", "rash",
		"rass", "rast", "rate", "rath", "rati", "rato", "rave", "rawl", "raze", "rbor",
		"rcen", "rday", "rdle", "reac", "read", "reak", "real", "ream", "reap", "rear",
		"reas", "reat", "rebe", "rebo", "reca", "rece", "reci", "reck", "reco", "rect",
		"rede", "redi", "redu", "reed", "reel", "reet", "refe", "refi", "refo", "refu",
		"regi", "regu", "rehe", "rela", "rele", "reli", "reme", "remo", "rena", "rend",
		"rent", "repe", "repo", "repr", "repu", "resc", "resh", "resi", "reso", "resp",
		"ress", "rest", "resu", "reta", "rete", "reti", "retr", "retu", "reve", "revi",
		"rgen", "rget", "rian", "riat", "ribe", "ribo", "rica", "rice", "rich", "rick",
		"rico", "rict", "ride", "rief", "rien", "rier", "rife", "riff", "rifl", "rift",
		"righ", "rike", "rill", "rime", "rina", "rine", "ring", "rink", "riot", "ripe",
		"ripo", "ript", "rise", "rish", "risk", "rist", "rita", "rith", "rive", "rize",
		"rlet", "rman", "rmor", "rnet", "road", "roar", "robe", "robo", "rock", "rode",
		"roga", "roid", "roil", "roke", "role", "roll", "roma", "romb", "romo", "rone",
		"rong", "roni", "ront", "rook", "room", "roon", "root", "rope", "roph", "rose",
		"ross", "roth", "roud", "roug", "roun", "roup", "rous", "rout", "rove", "rovi",
		"rowd", "rown", "rran", "rren", "rres", "rror", "rrow", "rson", "rter", "rtle",
		"rtun", "rude", "ruel", "ruin", "rule", "rumb", "rump", "rung", "runk", "runt",
		"rupt", "rush", "russ", "rust", "ruth", "sack", "sacr", "sade", "safe", "sage",
		"sail", "sake", "sala", "sale", "sali", "salo", "salt", "salu", "salv", "sand",
		"sane", "sang", "sani", "sano", "sant", "sary", "sate", "sati", "saur", "scal",
		"scam", "scan", "scar", "scen", "sche", "scho", "scin", "scon", "scop", "scor",
		"scot", "scou", "scra", "scri", "scro", "scru", "scur", "scus", "sday", "seal",
		"sear", "seas", "seat", "secr", "sect", "secu", "sede", "sedi", "seed", "sele",
		"self", "sell", "semi", "send", "seni", "sens", "sent", "sequ", "serf", "seri",
		"sers", "sert", "serv", "sess", "sett", "seum", "seve", "shad", "shal", "sham",
		"shan", "shap", "shar", "shea", "shee", "shel", "shie", "shin", "ship", "shir",
		"shoe", "shoo", "shop", "shor", "shot", "shou", "show", "shut", "sick", "side",
		"sigh", "sign", "sile", "sili", "silv", "simi", "simu", "sing", "sink", "sion",
		"sire", "sist", "site", "size", "skel", "sket", "skin", "skir", "slam", "slap",
		"slav", "slea", "slee", "slid", "slim", "slin", "slip", "slog", "slow", "slum",
		"sman", "smit", "snap", "snip", "snow", "soci", "sock", "sode", "soft", "sola",
		"sold", "sole", "soli", "solo", "solu", "solv", "soma", "some", "song", "soph",
		"sorb", "sore", "sort", "sote", "soul", "sour", "spac", "span", "spar", "spat",
		"spec", "spee", "spel", "spen", "spic", "spin", "spir", "spit", "spla", "spli",
		"spok", "spon", "spoo", "spot", "spri", "spro", "spun", "spur", "sput", "squa",
		"sque", "squi", "ssen", "sset", "stab", "staf", "stag", "stai", "stak", "stal",
		"stam", "stan", "star", "stat", "stay", "stea", "stel", "stem", "sten", "step",
		"ster", "stic", "stif", "stig", "stil", "stim", "stin", "stit", "stle", "stoc",
		"stol", "stom", "ston", "stop", "stor", "stow", "stra", "stre", "stri", "stro",
		"stru", "stud", "stum", "subm", "subs", "subt", "succ", "suck", "suit", "sult",
		"sume", "summ", "sump", "sund", "supp", "supr", "surd", "sure", "surg", "surr",
		"surv", "susc", "susp", "swea", "swim", "symb", "symp", "synt", "tabl", "tach",
		"tack", "tact", "tail", "tain", "take", "tale", "tali", "talk", "tall", "tame",
		"tamp", "tang", "tank", "tant", "tard", "tare", "targ", "tarn", "tase", "task",
		"tate", "tatu", "taut", "taxi", "teal", "team", "tear", "tech", "tect", "teen",
		"teer", "teet", "tele", "tell", "temp", "tena", "tenc", "tend", "teno", "tens",
		"tent", "terc", "tere", "terf", "term", "tern", "terr", "terv", "tess", "test",
		"tetr", "text", "thar", "them", "theo", "ther", "thet", "thic", "thin", "thod",
		"thol", "thon", "thor", "thra", "thre", "thri", "thro", "thru", "thun", "tice",
		"tick", "ticl", "tide", "tier", "tiff", "tige", "tile", "till", "tilt", "timb",
		"time", "tina", "tine", "ting", "tink", "tint", "tinu", "tion", "tire", "tiss",
		"tita", "titu", "tman", "tock", "toil", "toke", "told", "tole", "toll", "tomb",
		"tomo", "tone", "tono", "tool", "toon", "toot", "tore", "torn", "torr", "tort",
		"tory", "toss", "tour", "tout", "town", "tput", "trac", "trad", "trag", "trai",
		"tram", "tran", "trap", "trat", "trav", "tray", "trea", "tree", "trem", "tren",
		"tres", "tria", "trib", "tric", "trig", "tril", "trim", "trin", "trio", "trip",
		"triv", "trix", "trod", "trol", "tron", "trop", "trot", "trou", "troy", "truc",
		"trud", "true", "trum", "trun", "trus", "tter", "ttle", "tton", "tube", "tude",
		"tume", "tune", "turb", "turd", "ture", "turn", "tuss", "tute", "twee", "twin",
		"type", "uage", "uard", "uary", "uate", "ubbl", "ubli", "ucle", "udge", "uenc",
		"uest", "uffi", "uffl", "ught", "uite", "ulat", "ulge", "ulle", "ulti", "umbl",
		"umin", "umme", "ummy", "umor", "umph", "unce", "unch", "uncl", "unct", "unde",
		"undr", "unge", "ungu", "unic", "unio", "unit", "unkn", "unsp", "unty", "upid",
		"uple", "ural", "urch", "uret", "urge", "urre", "urry", "urse", "usse", "ussi",
		"uste", "ustl", "ustr", "utch", "uten", "uter", "util", "utin", "utte", "vaca",
		"vacu", "vade", "vail", "vain", "vale", "vali", "valu", "vamp", "vane", "vani",
		"vant", "vasc", "vate", "veal", "vect", "vehi", "veil", "vein", "velo", "vend",
		"vene", "veni", "vent", "verb", "verd", "verh", "veri", "verl", "vern", "vers",
		"vert", "vest", "vice", "vici", "vict", "vide", "view", "vile", "vill", "vinc",
		"vine", "viol", "virt", "visi", "vite", "vive", "voca", "void", "voke", "volt",
		"volu", "vore", "vote", "vour", "wade", "wage", "wain", "wait", "wake", "walk",
		"wall", "want", "ward", "ware", "warm", "warn", "warp", "warr", "wart", "wash",
		"wast", "wave", "weal", "wear", "weed", "week", "weep", "weet", "well", "west",
		"wher", "whip", "whis", "whiz", "whol", "whop", "wich", "wick", "wide", "wife",
		"wild", "will", "wind", "wine", "wing", "wipe", "wire", "wise", "wish", "wist",
		"wolf", "wood", "wool", "word", "work", "worl", "worm", "worn", "wort", "wrap",
		"writ", "wurs", "xact", "xper", "xplo", "xtra", "yard", "yarn", "year", "ymph",
		"yoff", "yond", "youn", "yout", "zeal", "zero", "zest", "zone", "zoom", "zzle"
	};

	private static final String[] l5g = {
		"abort", "abuse", "actic", "actor", "adult", "advan", "agent", "agree", "alleg", "allow",
		"ament", "ameri", "anger", "anima", "ankle", "apple", "apply", "arden", "arena", "arren",
		"assoc", "atern", "ation", "attle", "audio", "austr", "avail", "avern", "award", "battl",
		"beach", "beard", "belly", "birth", "black", "blame", "blast", "blaze", "blend", "blind",
		"block", "blood", "board", "brack", "brain", "bread", "break", "brick", "broad", "brook",
		"broth", "brown", "brute", "build", "built", "busin", "cargo", "cause", "cease", "ceive",
		"centr", "centu", "chain", "chair", "champ", "chanc", "chang", "chant", "chara", "charg",
		"chase", "cheap", "check", "cheek", "cheer", "chees", "cheme", "chest", "chief", "child",
		"chrom", "chron", "circl", "circu", "civil", "claim", "class", "clean", "clear", "clerk",
		"cliff", "cline", "clock", "close", "cloth", "clown", "clude", "coach", "coast", "colon",
		"comma", "commo", "compa", "compe", "confe", "confi", "conne", "const", "consu", "conta",
		"conte", "contr", "conve", "cosmo", "counc", "count", "cours", "court", "cover", "crack",
		"craft", "crash", "crawl", "craze", "cream", "creas", "creat", "creek", "cresc", "crest",
		"crime", "cross", "crowd", "crown", "crude", "crush", "crust", "crypt", "cture", "curio",
		"curre", "curve", "custo", "cycle", "dairy", "dance", "death", "decid", "defen", "democ",
		"depar", "depen", "depth", "desig", "devel", "direc", "disco", "distr", "doubt", "draft",
		"drama", "dream", "dress", "drift", "drink", "drive", "drome", "drone", "dynam", "ealth",
		"earin", "earth", "eason", "easur", "educa", "egion", "egree", "eight", "elbow", "elect",
		"elite", "ellow", "emand", "ember", "emplo", "enemy", "energ", "enjoy", "enter", "eport",
		"equen", "equip", "ermin", "error", "escri", "estab", "estig", "estim", "ethan", "event",
		"exert", "expec", "exten", "faith", "famil", "fashi", "fathe", "fault", "featu", "felon",
		"fever", "field", "fight", "figur", "final", "finan", "finge", "flake", "flare", "flash",
		"flavo", "flesh", "flict", "flood", "floor", "flush", "focus", "force", "fores", "forge",
		"found", "frame", "fraud", "frien", "front", "frown", "fruit", "futur", "gener", "ghost",
		"glass", "globe", "gover", "grace", "grade", "grain", "grand", "grant", "graph", "grass",
		"green", "greet", "gress", "grind", "groom", "groun", "group", "grove", "guard", "guest",
		"guide", "guile", "hance", "happe", "happy", "haven", "heart", "heath", "highw", "histo",
		"hollo", "honey", "horse", "hotel", "house", "human", "icate", "icide", "icipa", "iddle",
		"ident", "image", "imate", "immun", "impor", "index", "indus", "iness", "infor", "insta",
		"instr", "integ", "inter", "intro", "inves", "islan", "issue", "isten", "itude", "janit",
		"joint", "judge", "junct", "jungl", "knife", "knuck", "labor", "larce", "large", "later",
		"laugh", "learn", "lease", "legit", "level", "light", "limit", "litic", "llage", "llege",
		"lleng", "llion", "locat", "lunch", "macro", "major", "manag", "manor", "march", "marke",
		"marsh", "mason", "maste", "match", "mater", "matio", "meado", "media", "medic", "membe",
		"membr", "merce", "merge", "merse", "metal", "meter", "micro", "might", "milit", "milli",
		"minat", "minor", "minut", "mmuni", "model", "money", "monit", "month", "motor", "mount",
		"mouth", "movie", "music", "nance", "natio", "natur", "neigh", "neral", "neutr", "ngine",
		"night", "niver", "noise", "novel", "nsist", "ntern", "nterp", "numer", "nurse", "ocean",
		"offer", "offic", "ollar", "ology", "ommun", "ontra", "opera", "orbit", "order", "organ",
		"otent", "other", "ouble", "ounce", "paign", "panel", "paper", "paren", "party", "patch",
		"pater", "peace", "peopl", "perio", "phase", "phone", "photo", "piece", "pilot", "pital",
		"pitch", "place", "plain", "plane", "plant", "plate", "plent", "plete", "plica", "plore",
		"point", "polic", "polit", "posit", "pound", "power", "ppear", "pport", "pract", "prehe",
		"press", "price", "pride", "prima", "princ", "pring", "print", "prise", "prize", "proba",
		"proce", "produ", "progr", "proje", "prone", "proof", "prope", "prosp", "prote", "proto",
		"prove", "publi", "pulse", "quart", "queen", "queer", "quent", "quest", "queue", "quick",
		"quiet", "quire", "radio", "raise", "ranch", "range", "ratio", "reach", "recom", "recor",
		"reduc", "refer", "refor", "regul", "rench", "repla", "reply", "repor", "repub", "resid",
		"ridge", "right", "river", "roast", "roduc", "round", "route", "saint", "salut", "sault",
		"scale", "scape", "scene", "schoo", "scien", "scope", "score", "scrap", "screw", "searc",
		"seaso", "secur", "sembl", "senat", "sense", "shack", "shake", "shame", "shape", "share",
		"shark", "sharp", "sheep", "sheet", "shell", "shift", "shine", "shire", "shirt", "shock",
		"shore", "short", "shrap", "sight", "simil", "skate", "skill", "skull", "sland", "slate",
		"sleep", "smart", "smash", "smile", "smith", "smoke", "socia", "solut", "solve", "sound",
		"sourc", "space", "speak", "spear", "spect", "speed", "spell", "spend", "spill", "spine",
		"spire", "spite", "splay", "split", "spoil", "sport", "sprin", "spruc", "squad", "squar",
		"ssist", "stabl", "stack", "staff", "stage", "stain", "stair", "stake", "stall", "stamp",
		"stand", "start", "state", "stati", "stead", "steal", "steam", "steel", "steep", "steer",
		"stick", "still", "stock", "stone", "store", "storm", "story", "stove", "strai", "stran",
		"strat", "stree", "strik", "strip", "struc", "study", "stuff", "stunt", "style", "suade",
		"subst", "sugar", "super", "sweet", "sword", "table", "taste", "teach", "techn", "tempt",
		"teria", "terra", "thank", "theat", "theme", "theor", "therm", "think", "thres", "throa",
		"throw", "thumb", "tight", "title", "toast", "tooth", "touch", "tower", "toxic", "track",
		"tract", "trade", "trail", "train", "tramp", "trans", "trave", "treat", "treme", "trend",
		"trial", "trick", "trict", "troll", "trong", "troop", "troph", "truck", "truct", "trust",
		"truth", "tutor", "twist", "ublic", "uffer", "ultur", "ument", "under", "union", "unive",
		"ustle", "valen", "valle", "value", "veget", "velop", "venue", "verge", "veter", "video",
		"villa", "ville", "visit", "vista", "voice", "volve", "waist", "waste", "watch", "water",
		"whack", "whale", "wheel", "whelm", "whimp", "white", "whole", "winte", "witch", "woman",
		"world", "worth", "wound", "wrack", "wreck", "wrist", "write", "yield", "youth", "ystem"
	};

	private HashMap<String,Integer> h4g; // for 4-gram lookup
	private HashMap<String,Integer> h5g; //     5-gram

	public GramMap ( ) {
		h4g = new HashMap<String,Integer>();
		for (int i = 0; i < l4g.length; i++)
			h4g.put(l4g[i],i);
		h5g = new HashMap<String,Integer>();
		for (int i = 0; i < l5g.length; i++)
			h5g.put(l5g[i],i);
	}

	public String decode4g ( int x ) { return (x < l4g.length) ? l4g[x] : ""; }

	public String decode5g ( int x ) { return (x < l5g.length) ? l5g[x] : ""; }

	public int encode4g ( String s ) { return (h4g.containsKey(s)) ? h4g.get(s) : -1; }

	public int encode5g ( String s ) { return (h5g.containsKey(s)) ? h5g.get(s) : -1; }

	public int count4g ( ) { return l4g.length; }

	public int count5g ( ) { return l5g.length; }

	public static void main ( String[] av ) {
		GramMap gm = new GramMap();
		System.out.println(gm.count4g() + " 4gs, " + gm.count5g() + " 5gs");
		String[] a = { "1" , "2" , "4" , "8" , "16" , "writ" , "world" , "xray" , "battle" };
		if (av.length > 0) a = av;
		for (int i = 0; i < a.length; i++) {
			String x = a[i]; 
			System.out.print(x);
			if (x.length() == 0)
				System.out.println(" (null)");
			else if (Character.isDigit(x.charAt(0))) {
				int k = Integer.parseInt(x);
				System.out.println(": 4g= [" + gm.decode4g(k) + "]");
				System.out.print(x);
				System.out.println(": 5g= [" + gm.decode5g(k) + "]");
			}
			else if (x.length() == 4)
				System.out.println(": 4g =" + gm.encode4g(x));
			else if (x.length() == 5)
				System.out.println(": 5g =" + gm.encode5g(x));
			else
				System.out.println(": unrecognized");
		}
	}
}

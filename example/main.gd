
extends Node
const LEADERBOARD_ID	= "XXXXXXXXXXXXXXXXXX"	# Place your Leaderboard ID here
var gps

func _ready():
	if(Globals.has_singleton("GooglePlayService")):
		gps		= Globals.get_singleton("GooglePlayService")
		gps.init()
	else:
		gps	= null
	get_node("btSignIn").connect("pressed", self, "_on_btSignIn_pressed")
	get_node("btSignOut").connect("pressed", self, "_on_btSignOut_pressed")
	get_node("btLbSubmit").connect("pressed", self, "_on_btLbSubmit_pressed")
	get_node("btLbShow").connect("pressed", self, "_on_btLbShow_pressed")

func _on_btSignIn_pressed():
	if(null == gps):
		return
	gps.signin()
	
func _on_btSignOut_pressed():
	if(null == gps):
		return
	gps.signout()

func _on_btLbSubmit_pressed():
	if(null == gps):
		return
	gps.lbSubmit(LEADERBOARD_ID, 123)
	
func _on_btLbShow_pressed():
	if(null == gps):
		return
	gps.lbShow(LEADERBOARD_ID)


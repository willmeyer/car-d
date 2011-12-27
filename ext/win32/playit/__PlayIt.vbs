' ----USer-editable variables----
LAME_EXE = "lame.exe" ' This default value requires LAME to be on your path

' ----Script code----
WScript.echo "*** PlayIt ***"
On Error Resume Next

' Validate
If WScript.Arguments.Unnamed.Count = 0 Then
    WScript.echo "Usage: <file | URL> what to play"
    WScript.echo "    options:"
    WScript.echo ""
    WScript.echo "This script will return the following exit codes:"
    WScript.echo "     0: all OK"
    WScript.echo "    -1: error"
    WScript.quit (-1)
End If

' Common objects
Set shell = WScript.CreateObject("WScript.Shell")

' What are we playing?
toPlay = ""
For Each arg In WScript.Arguments.Unnamed
    toPlay = toPlay & arg & " "     	
Next
'toPlay = "M:\\audio\\mirror\\main\\Alpha\\Come From Heaven\\(01)Alpha-My_Things.128k.mp3" 
'toPlay = "http://lacabeza.sptm.org/media/audio/mirror/main/Tiga/DJ-Kicks%20EP%20(Hot%20in%20Herre%20_%20Man%20Hrdina)/(2)Tiga-Hot_in_Herre.128k.mp3"
WScript.echo "Will play: " & toPlay

' Create the player, killing any existing ones first (because sometimes they get left in a bad state from previouys plays)
WScript.echo "Terminating prior players..."
KillPlayers
WScript.Sleep(1000)
WScript.echo "Initializing player..."
Set player = WScript.CreateObject("WMPlayer.OCX")
If player Is Nothing Then
    WScript.echo "The windows media player API does not seem to be installed, please download and install it from MS"
    WScript.Quit (-1)
End If
player.playerApplication.switchToControl

' Play the URI
WScript.echo "Playing URI..."
player.OpenPlayer toPlay

' All done
player.close
Set player = Nothing
Set shell = Nothing
'KillPlayers
WScript.quit (0)
WScript.echo "Done"

Sub KillPlayers
	strComputer = "."
	Set objWMIService = GetObject("winmgmts:" _
		& "{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
	Set colProcesses = objWMIService.ExecQuery _
		("Select * from Win32_Process Where Name = 'wmplayer.exe'")
	For Each objProcess in colProcesses
		objProcess.Terminate 
	Next
End Sub
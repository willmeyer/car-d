' ----USer-editable variables----
LAME_EXE = "lame.exe" ' This default value requires LAME to be on your path

' ----Script code----
WScript.echo "*** SpeakIt ***"
On Error Resume Next

' Validate
If WScript.Arguments.Unnamed.Count = 0 Then
    WScript.echo "Usage: <options> what to say"
    WScript.echo "    options:"
    WScript.echo "      /file:name   Specify the name of an output file, without an extension."
    WScript.echo "                   Two files, name.wav and name.mp3, will be generated.  This"
    WScript.echo "                   requires that LAME.EXE be available on your PATH or that you"
    WScript.echo "                   change the LAME_EXE variable in this script file."
    WScript.echo ""
    WScript.echo "This script will return the following exit codes:"
    WScript.echo "     0: all OK"
    WScript.echo "    -1: error"
    WScript.quit (-1)
End If

' Common objects
Set shell = WScript.CreateObject("WScript.Shell")

' Are we writing to file?  And encoding?
toFile = False
'outFileName = "SpeakIt.output.wav"
outFileName = WScript.Arguments.Named ("file")
If (Not outFileName = "") Then
    toFile = True
End If 

' What are we speaking?
toSay = ""
For Each arg In WScript.Arguments.Unnamed
    toSay = toSay & arg & " "     	
Next 
WScript.echo "Will speak: " & toSay


' Create the voice
WScript.echo "Initializing voice..."
Set voice = WScript.CreateObject("Sapi.SpVoice")
If voice Is Nothing Then
    WScript.echo "The speech API does not seem to be installed, please download and install it from MS"
    WScript.Quit (-1)
End If

' Create a file for output if needed
If (toFile) Then
    WScript.echo "Opening file stream: " & outFileName & ".wav"
    Set fileStream = WScript.CreateObject("Sapi.SpFileStream")
    fileStream.Format.Type = 35 'SPSF_22kHz16BitMono
    fileStream.Open outFileName & ".wav", 3
    Set voice.AudioOutputStream = fileStream
End If

' Speak and wait until done
WScript.echo "Generating speech..."
voice.Volume = 100
voice.Speak toSay
voice.WaitUntilDone 10000

' Close the stream and set voice back to speaking:
If toFile Then
    fileStream.Close
    Set voice.AudioOutputStream = Nothing
End If

' If writing to a file, encode to MP3 as well
If (toFile) Then
    WScript.echo "Encoding WAV to MP3: " & outFileName & ".mp3"
    cmd = LAME_EXE & " " & outFileName & ".wav -o " & outFileName & ".mp3"
    retVal = shell.Run (cmd, 1, True)
    'WScript.echo "LAME Err: " & Err.Number & ", Return code: " & retVal
    If (retVal <> 0) Or (Err.Number <> 0) Then
        WScript.echo "Problem running LAME, make sure it is on your path (or that you set the LAME_EXE variable in this script."
        WScript.Quit (-1)
    End If
End If

' All done
Set voice = Nothing
Set shell = Nothing
WScript.quit (0)
WScript.echo "Done"

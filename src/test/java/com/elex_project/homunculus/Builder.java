/*
 * Project Homunculus
 *
 * Copyright (c) 2017-2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.homunculus;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.KeySignature;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Elex on 2015-11-24.
 */
public class Builder {
    public static void main(String... args) throws IOException {

        File folder = new File("resources");
        File[] files = folder.listFiles((dir, name)
                -> name.endsWith(".mid") && name.startsWith("T"));

        MidiFile midiOut = new MidiFile();
        MidiTrack newTrack = new MidiTrack();
        boolean set = false;
        for (File midiFile : files) {
            int id = Integer.valueOf(midiFile.getName().substring(1).replace(".mid", ""));
            MidiFile midi = new MidiFile(midiFile);
            if (!set){
                midiOut.setResolution(midi.getResolution());
                midiOut.setType(midi.getType());
                newTrack.insertEvent(new KeySignature(0, 0, 2, 0));
                newTrack.insertEvent(new TimeSignature(0,0,3,4,TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION));
                set = true;
            }

            List<MidiTrack> tracks = midi.getTracks();
            for (MidiTrack track : tracks){
                newTrack.insertEvent(new Text(id * midiOut.getResolution()*3, 0, String.valueOf(id)));
                Iterator<MidiEvent> events = track.getEvents().iterator();
                while (events.hasNext()) {
                    MidiEvent event = events.next();
                    if (event instanceof NoteOn) {
                        long tick = event.getTick();
                        tick += id * midiOut.getResolution()*3 - midi.getResolution()*3;
                        int channel = ((NoteOn) event).getChannel();
                        int note = ((NoteOn) event).getNoteValue();
                        int velocity = ((NoteOn) event).getVelocity();
                        NoteOn newEvent = new NoteOn(tick, channel, note, velocity);
                        newTrack.insertEvent(newEvent);
                    } else if (event instanceof NoteOff) {
                        long tick = event.getTick();
                        tick += id * midiOut.getResolution()*3 - midi.getResolution()*3;
                        int channel = ((NoteOff) event).getChannel();
                        int note = ((NoteOff) event).getNoteValue();
                        int velocity = ((NoteOff) event).getVelocity();
                        NoteOff newEvent = new NoteOff(tick, channel, note, velocity);
                        newTrack.insertEvent(newEvent);
                    }

                }

            }

        }
        midiOut.addTrack(newTrack);
        File output = new File(new File(folder, "processed"), "all_T.mid");
        midiOut.writeToFile(output);

    }
}

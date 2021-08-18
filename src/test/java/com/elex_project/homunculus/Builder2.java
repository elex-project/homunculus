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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Elex on 2015-11-24.
 */
public class Builder2 {
    public static void main(String... args) throws IOException {
        HashMap<Integer, ArrayList<MidiEvent>> map = new HashMap<>();

        File folder = new File("resources");
        File file = new File(folder, "M_L.mid");
        MidiFile midiFile = new MidiFile(file);
        System.out.println("Resolution: " + midiFile.getResolution());
        for (MidiTrack track : midiFile.getTracks()) {
            Iterator<MidiEvent> events = track.getEvents().iterator();
            while(events.hasNext()) {
                MidiEvent event = events.next();
                if (event instanceof NoteOn) {
                    long tick = event.getTick();
                    int measure = (int) (tick / (midiFile.getResolution()*3)) +1;
                    tick = tick % (midiFile.getResolution()*3);
                    int channel = ((NoteOn) event).getChannel();
                    int note = ((NoteOn) event).getNoteValue();
                    int velocity = ((NoteOn) event).getVelocity();
                    if (!map.containsKey(measure)) {
                        map.put(measure, new ArrayList<>());
                    }
                    map.get(measure).add(new NoteOn(tick, channel, note, velocity));
                } else if (event instanceof NoteOff) {
                    long tick = event.getTick();
                    int measure = (int) (tick / (midiFile.getResolution()*3)) +1;
                    tick = tick % (midiFile.getResolution()*3);
                    int channel = ((NoteOff) event).getChannel();
                    int note = ((NoteOff) event).getNoteValue();
                    int velocity = ((NoteOff) event).getVelocity();
                    if (!map.containsKey(measure)) {
                        map.put(measure, new ArrayList<>());
                    }
                    map.get(measure).add(new NoteOff(tick, channel, note, velocity));
                } else {
                    System.out.println(event.toString());
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        Iterator<Integer> keys = map.keySet().iterator();
        sb.append("private static int[][][] array = new int[][][]{\n");
        while(keys.hasNext()){
            Integer key = keys.next();
            sb.append("\tnew int[][]{ //" + key + "\n");
            ArrayList<MidiEvent> events = map.get(key);
            for (MidiEvent event : events) {
                if (event instanceof NoteOn) {
                    sb.append("\t\tnew int[]{" + event.getTick() + ", "
                            + ((NoteOn) event).getChannel() + ", " +
                            ((NoteOn) event).getNoteValue() + ", "
                            + ((NoteOn) event).getVelocity() + "},\n");
                } else if (event instanceof NoteOff) {
                    sb.append("\t\tnew int[]{-" + event.getTick() + ", "
                            + ((NoteOff) event).getChannel() + ", " +
                            ((NoteOff) event).getNoteValue() + ", "
                            + ((NoteOff) event).getVelocity() + "},\n");
                }
                //System.out.println(event.toString());
            }
            sb.append("\t},\n");
        }
        sb.append("};\n");
        File out = new File(folder, file.getName()+".java");
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        writer.write(sb.toString());
        writer.close();
    }
}

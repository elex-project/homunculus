/*
 * Project Homunculus
 *
 * Copyright (c) 2017-2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.homunculus;

import com.elex_project.jujak.JSONable;
import com.elex_project.abraxas.Random;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.KeySignature;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Elex on 2015-11-24.
 */
@Slf4j
public final class SongWriter implements JSONable {
	private static final String K_BPM = "비퓀";
	private static final String K_RAND = "랜드";

	public static final int SIZE_16 = 16;
	public static final int SIZE_32 = 32;

	private MidiFile midiFile;

	private float bpm = 100;
	private int[] diceSet;

	public SongWriter() {
		this(SIZE_32);
	}

	public SongWriter(JSONObject json) {
		midiFile = new MidiFile();
		midiFile.setResolution(960);
		midiFile.setType(1);

		this.bpm = json.getFloat(K_BPM);
		JSONArray array = json.getJSONArray(K_RAND);
		this.diceSet = new int[array.length()];
		for (int i = 0; i < array.length(); i++) {
			this.diceSet[i] = array.getInt(i);
		}
	}

	private SongWriter(int measureSize) {
		if (measureSize != SIZE_16 && measureSize != SIZE_32) throw new IllegalArgumentException();

		midiFile = new MidiFile();
		midiFile.setResolution(960);
		midiFile.setType(1);

		diceSet = new int[measureSize];
		set();
	}


	public void set() {
		for (int m = 1; m <= diceSet.length; m++) {
			set(m);
		}
	}

	public void setBpm(float bpm) {
		this.bpm = bpm;
	}

	/**
	 * 해당 마디의 주사위 번호를 반환
	 *
	 * @param measure 1~32
	 * @return
	 */
	public int get(int measure) {
		if (measure <= 0 || measure > 32) throw new IllegalArgumentException();
		return diceSet[measure - 1];
	}

	/**
	 * 해당 마디에 랜덤으로 주사위를 지정
	 *
	 * @param measure 1~32
	 */
	public void set(int measure) {
		if (measure <= 0 || measure > 32) {
			log.debug("Measure = " + measure);
			throw new IllegalArgumentException();
		}

		int dice;
		if (measure <= 16) {
			dice = Random.nextInt(2, 12);
			//dice = (Math.abs(random.nextInt()) % 11) + 2;
		} else {
			dice = Random.nextInt(1, 6);
			//dice = (Math.abs(random.nextInt()) % 6) + 1;
		}
		set(measure, dice);
	}

	/**
	 * 해당 마디에 임의의 주사위 숫자를 지정
	 *
	 * @param measure 1~32
	 * @param dice    2~12 for Minuet, 1~6 for Trio
	 */
	public void set(int measure, int dice) {
		if (measure >= 1 && measure <= 16) {
			if (dice < 2 || dice > 12) throw new IllegalArgumentException();
		} else if (measure <= 32) {
			if (dice < 1 || dice > 6) throw new IllegalArgumentException();
		} else {
			throw new IllegalArgumentException();
		}
		diceSet[measure - 1] = dice;
	}


	/**
	 * 미디 파일을 작성
	 */
	private void build() {
		MidiTrack[] tracks = new MidiTrack[3];
		tracks[0] = new MidiTrack();//meta
		tracks[1] = new MidiTrack();//right
		tracks[2] = new MidiTrack();//left

		tracks[0].insertEvent(new KeySignature(0, 0, 2, 0));
		tracks[0].insertEvent(new TimeSignature(0, 0, 3, 4,
				TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION));
		Tempo tempo = new Tempo();
		tempo.setBpm(bpm);
		tracks[0].insertEvent(tempo);

		try {
			for (int measure = 1; measure <= diceSet.length; measure++) {

				int[][][] events = MidiTable.getMeasure(measure, diceSet[measure - 1]);
				addTo(tracks, midiFile.getResolution(), measure, events);

				if (measure>=1 && measure<=16) { // 3부는 1부와 동일한 미뉴엣
					addTo(tracks, midiFile.getResolution(), measure+32, events);
				}

			}
			for (MidiTrack track : tracks) {
				midiFile.addTrack(track);
			}

		} catch (Exception e) {
			log.error("Oops!", e);
		}
	}

	/**
	 * 미디 파일을 작성한 후 파일로 저장한다.
	 *
	 * @param file
	 * @throws IOException
	 */
	public void saveTo(File file) throws IOException {
		build();
		midiFile.writeToFile(file);
	}


	static void addTo(MidiTrack[] tracks, int resolution, int measure, int[][][] events) {
		for (int tr = 1; tr <= 2; tr++) { // i:0 = right, i:1 = left
			for (int[] e : events[tr - 1]) {
				int tick = e[0];
				int channel = e[1];
				int note = e[2];
				int velocity = e[3];

				if (tick < 0) { // tick: note off
					tick *= -1;
					tick += (measure - 1) * resolution * 3;// 3/4박자니까 마디 수에 3을 곱한다.
					NoteOff event = new NoteOff(tick, channel, note, velocity);
					tracks[tr].insertEvent(event);

				} else { // note on
					tick += (measure - 1) * resolution * 3;
					NoteOn event = new NoteOn(tick, channel, note, velocity);
					tracks[tr].insertEvent(event);
				}
			}
		}
	}


	private static MidiFile prepareMeasure(int measure, int[][][] events) {
		MidiFile midiFile = new MidiFile();
		midiFile.setResolution(960);
		midiFile.setType(1);

		MidiTrack[] tracks = new MidiTrack[3];
		tracks[0] = new MidiTrack();//meta
		tracks[1] = new MidiTrack();//right
		tracks[2] = new MidiTrack();//left

		tracks[0].insertEvent(new KeySignature(0, 0, 2, 0));
		tracks[0].insertEvent(new TimeSignature(0, 0, 3, 4,
				TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION));

		//int[][][] events = MidiTable.getMinuet(id);
		addTo(tracks, midiFile.getResolution(), measure, events);

		for (MidiTrack track : tracks) {
			midiFile.addTrack(track);
		}

		return midiFile;
	}

	/**
	 * 각 마디별로 미디 파일을 작성한다. 프리뷰 용도
	 *
	 * @param id 1~176
	 * @return
	 */
	public static MidiFile prepareMinuetFile(int id) {
		return prepareMeasure(1, MidiTable.getMinuet(id));
	}

	/**
	 * 각 마디별로 미디 파일을 작성한다. 프리뷰 용도
	 *
	 * @param id 1~96
	 * @return
	 */
	public static MidiFile prepareTrioFile(int id) {
		return prepareMeasure(1, MidiTable.getTrio(id));
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put(K_BPM, this.bpm);
		JSONArray array = new JSONArray();
		for (int item : this.diceSet) {
			array.put(item);
		}
		json.put(K_RAND, array);
		return json;
	}
}

package com.example.sightreadingapp.data.models

import com.example.sightreadingapp.R

// chose an enum for both of these to enforce consistency
enum class NoteResourcesAndAnswer(val correctNote: NoteOptions, val correctAccidental: Accidentals, val drawableResource: Int) {
    TREBLE_NOTE_A_LEDGER(NoteOptions.A, Accidentals.NONE, R.drawable.treble_note_a_ledger),
    TREBLE_NOTE_A_SPACE(NoteOptions.A, Accidentals.NONE, R.drawable.treble_note_a_space),
    TREBLE_NOTE_B_LINE(NoteOptions.B, Accidentals.NONE, R.drawable.treble_note_b_line),
    TREBLE_NOTE_C_SPACE(NoteOptions.C, Accidentals.NONE, R.drawable.treble_note_c_space),
    TREBLE_NOTE_D_BELOW(NoteOptions.D, Accidentals.NONE, R.drawable.treble_note_d_below),
    TREBLE_NOTE_D_LINE(NoteOptions.D, Accidentals.NONE, R.drawable.treble_note_d_line),
    TREBLE_NOTE_E_BOTTOM(NoteOptions.E, Accidentals.NONE, R.drawable.treble_note_e_bottom),
    TREBLE_NOTE_E_TOP_SPACE(NoteOptions.E, Accidentals.NONE, R.drawable.treble_note_e_top_space),
    TREBLE_NOTE_F_SPACE(NoteOptions.F, Accidentals.NONE, R.drawable.treble_note_f_space),
    TREBLE_NOTE_F_TOP_LINE(NoteOptions.F, Accidentals.NONE, R.drawable.treble_note_f_top_line),
    TREBLE_NOTE_G_ABOVE(NoteOptions.G, Accidentals.NONE, R.drawable.treble_note_g_above),
    TREBLE_NOTE_G_LINE(NoteOptions.G, Accidentals.NONE, R.drawable.treble_note_g_line),

    TREBLE_NOTE_A_LEDGER_FLAT(NoteOptions.A, Accidentals.FLAT, R.drawable.treble_note_a_ledger_flat),
    TREBLE_NOTE_A_SPACE_FLAT(NoteOptions.A, Accidentals.FLAT, R.drawable.treble_note_a_space_flat),
    TREBLE_NOTE_B_LINE_FLAT(NoteOptions.B, Accidentals.FLAT, R.drawable.treble_note_b_line_flat),
    TREBLE_NOTE_C_SPACE_FLAT(NoteOptions.C, Accidentals.FLAT, R.drawable.treble_note_c_space_flat),
    TREBLE_NOTE_D_BELOW_FLAT(NoteOptions.D, Accidentals.FLAT, R.drawable.treble_note_d_below_flat),
    TREBLE_NOTE_D_LINE_FLAT(NoteOptions.D, Accidentals.FLAT, R.drawable.treble_note_d_line_flat),
    TREBLE_NOTE_E_BOTTOM_FLAT(NoteOptions.E, Accidentals.FLAT, R.drawable.treble_note_e_bottom_flat),
    TREBLE_NOTE_E_TOP_SPACE_FLAT(NoteOptions.E, Accidentals.FLAT, R.drawable.treble_note_e_top_space_flat),
    TREBLE_NOTE_F_SPACE_FLAT(NoteOptions.F, Accidentals.FLAT, R.drawable.treble_note_f_space_flat),
    TREBLE_NOTE_F_TOP_LINE_FLAT(NoteOptions.F, Accidentals.FLAT, R.drawable.treble_note_f_top_line_flat),
    TREBLE_NOTE_G_ABOVE_FLAT(NoteOptions.G, Accidentals.FLAT, R.drawable.treble_note_g_above_flat),
    TREBLE_NOTE_G_LINE_FLAT(NoteOptions.G, Accidentals.FLAT, R.drawable.treble_note_g_line_flat),

    TREBLE_NOTE_A_LEDGER_SHARP(NoteOptions.A, Accidentals.SHARP, R.drawable.treble_note_a_ledger_sharp),
    TREBLE_NOTE_A_SPACE_SHARP(NoteOptions.A, Accidentals.SHARP, R.drawable.treble_note_a_space_sharp),
    TREBLE_NOTE_B_LINE_SHARP(NoteOptions.B, Accidentals.SHARP, R.drawable.treble_note_b_line_sharp),
    TREBLE_NOTE_C_SPACE_SHARP(NoteOptions.C, Accidentals.SHARP, R.drawable.treble_note_c_space_sharp),
    TREBLE_NOTE_D_BELOW_SHARP(NoteOptions.D, Accidentals.SHARP, R.drawable.treble_note_d_below_sharp),
    TREBLE_NOTE_D_LINE_SHARP(NoteOptions.D, Accidentals.SHARP, R.drawable.treble_note_d_line_sharp),
    TREBLE_NOTE_E_BOTTOM_SHARP(NoteOptions.E, Accidentals.SHARP, R.drawable.treble_note_e_bottom_sharp),
    TREBLE_NOTE_E_TOP_SPACE_SHARP(NoteOptions.E, Accidentals.SHARP, R.drawable.treble_note_e_top_space_sharp),
    TREBLE_NOTE_F_SPACE_SHARP(NoteOptions.F, Accidentals.SHARP, R.drawable.treble_note_f_space_sharp),
    TREBLE_NOTE_F_TOP_LINE_SHARP(NoteOptions.F, Accidentals.SHARP, R.drawable.treble_note_f_top_line_sharp),
    TREBLE_NOTE_G_ABOVE_SHARP(NoteOptions.G, Accidentals.SHARP, R.drawable.treble_note_g_above_sharp),
    TREBLE_NOTE_G_LINE_SHARP(NoteOptions.G, Accidentals.SHARP, R.drawable.treble_note_g_line_sharp),
}


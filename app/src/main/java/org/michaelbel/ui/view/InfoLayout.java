package org.michaelbel.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.seriespicker.R;

/**
 * Date: 19 MAR 2018
 * Time: 17:40 MSK
 *
 * @author Michael Bel
 */

public class InfoLayout extends FrameLayout {

    private LinearLayout linearLayout;

    private TextView genresText;

    private TextView originalName;
    private TextView originalNameText;

    private TextView originalCountry;
    private TextView originalCountryText;

    private TextView statusText;
    private TextView typeText;

    private TextView firstAirDate;
    private TextView lastAirDate;

    private TextView companiesText;
    private TextView homepageText;

    public InfoLayout(@NonNull Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, R.color.background));

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(linearLayout);

        TextView infoTitle = new TextView(context);
        infoTitle.setLines(1);
        infoTitle.setMaxLines(1);
        infoTitle.setSingleLine();
        infoTitle.setText(R.string.Info);
        infoTitle.setGravity(Gravity.CENTER_VERTICAL);
        infoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        infoTitle.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        infoTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        infoTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, 42, Gravity.START | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));
        linearLayout.addView(infoTitle);

//------Genres--------------------------------------------------------------------------------------

        TextView genresTitle = new TextView(context);
        genresTitle.setText(context.getString(R.string.GenresTitle));
        genresTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        genresTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        genresTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 0));
        linearLayout.addView(genresTitle);

        genresText = new TextView(context);
        genresText.setTextIsSelectable(true);
        genresText.setText(R.string.Loading);
        genresText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        genresText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 16));
        linearLayout.addView(genresText);

//------OriginalTitle and OriginalCountries---------------------------------------------------------

        LinearLayout layout0 = new LinearLayout(context);
        layout0.setOrientation(LinearLayout.HORIZONTAL);
        layout0.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 0));
        linearLayout.addView(layout0);

        // OriginalTitle

        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START, 1.0F));
        layout0.addView(titleLayout);

        originalName = new TextView(context);
        originalName.setText(context.getString(R.string.OriginalName));
        originalName.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        originalName.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        originalName.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        titleLayout.addView(originalName);

        originalNameText = new TextView(context);
        originalNameText.setTextIsSelectable(true);
        originalNameText.setText(R.string.Loading);
        originalNameText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        originalNameText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        titleLayout.addView(originalNameText);

        // OriginalCountries

        LinearLayout countriesLayout = new LinearLayout(context);
        countriesLayout.setOrientation(LinearLayout.VERTICAL);
        countriesLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START, 1.0F));
        layout0.addView(countriesLayout);

        originalCountry = new TextView(context);
        originalCountry.setText(context.getString(R.string.OriginalCountry));
        originalCountry.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        originalCountry.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        originalCountry.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        countriesLayout.addView(originalCountry);

        originalCountryText = new TextView(context);
        originalCountryText.setTextIsSelectable(true);
        originalCountryText.setText(R.string.Loading);
        originalCountryText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        originalCountryText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        countriesLayout.addView(originalCountryText);

//------Status and Type-----------------------------------------------------------------------------

        LinearLayout layout1 = new LinearLayout(context);
        layout1.setOrientation(LinearLayout.HORIZONTAL);
        layout1.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 16, 16, 0));
        linearLayout.addView(layout1);

        // Status

        LinearLayout statusLayout = new LinearLayout(context);
        statusLayout.setOrientation(LinearLayout.VERTICAL);
        statusLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START, 1.0F));
        layout1.addView(statusLayout);

        TextView statusTitle = new TextView(context);
        statusTitle.setText(context.getString(R.string.Status));
        statusTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        statusTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        statusTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        statusLayout.addView(statusTitle);

        statusText = new TextView(context);
        statusText.setTextIsSelectable(true);
        statusText.setText(R.string.Loading);
        statusText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        statusText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        statusLayout.addView(statusText);

        // Type

        LinearLayout typeLayout = new LinearLayout(context);
        typeLayout.setOrientation(LinearLayout.VERTICAL);
        typeLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START, 1.0F));
        layout1.addView(typeLayout);

        TextView typeTitle = new TextView(context);
        typeTitle.setText(context.getString(R.string.ShowType));
        typeTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        typeTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        typeTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        typeLayout.addView(typeTitle);

        typeText = new TextView(context);
        typeText.setTextIsSelectable(true);
        typeText.setText(R.string.Loading);
        typeText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        typeText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        typeLayout.addView(typeText);

//------FirstAirDate and LastAirDate----------------------------------------------------------------

        LinearLayout layout2 = new LinearLayout(context);
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout2.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 16, 16, 0));
        linearLayout.addView(layout2);

        // FirstAirDate

        LinearLayout firstDateLayout = new LinearLayout(context);
        firstDateLayout.setOrientation(LinearLayout.VERTICAL);
        firstDateLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START, 1.0F));
        layout2.addView(firstDateLayout);

        TextView firstDateTitle = new TextView(context);
        firstDateTitle.setText(context.getString(R.string.FirstAirDate));
        firstDateTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        firstDateTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        firstDateTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        firstDateLayout.addView(firstDateTitle);

        firstAirDate = new TextView(context);
        firstAirDate.setTextIsSelectable(true);
        firstAirDate.setText(R.string.Loading);
        firstAirDate.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        firstAirDate.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        firstDateLayout.addView(firstAirDate);

        // LastAirDate

        LinearLayout lastDateLayout = new LinearLayout(context);
        lastDateLayout.setOrientation(LinearLayout.VERTICAL);
        lastDateLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START, 1.0F));
        layout2.addView(lastDateLayout);

        TextView lastDateTitle = new TextView(context);
        lastDateTitle.setText(context.getString(R.string.LastAirDate));
        lastDateTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        lastDateTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        lastDateTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        lastDateLayout.addView(lastDateTitle);

        lastAirDate = new TextView(context);
        lastAirDate.setTextIsSelectable(true);
        lastAirDate.setText(R.string.Loading);
        lastAirDate.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        lastAirDate.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        lastDateLayout.addView(lastAirDate);

//------Companies-----------------------------------------------------------------------------------

        TextView companiesTitle = new TextView(context);
        companiesTitle.setText(context.getString(R.string.CompaniesTitle));
        companiesTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        companiesTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        companiesTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 16, 16, 16, 0));
        linearLayout.addView(companiesTitle);

        companiesText = new TextView(context);
        companiesText.setTextIsSelectable(true);
        companiesText.setText(R.string.Loading);
        companiesText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        companiesText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 0));
        linearLayout.addView(companiesText);

//------Homepage------------------------------------------------------------------------------------

        TextView homepageTitle = new TextView(context);
        homepageTitle.setText(context.getString(R.string.HomepageTitle));
        homepageTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        homepageTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        homepageTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 16, 16, 16, 0));
        linearLayout.addView(homepageTitle);

        homepageText = new TextView(context);
        homepageText.setTextIsSelectable(true);
        homepageText.setText(R.string.Loading);
        homepageText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        homepageText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 16));
        linearLayout.addView(homepageText);
    }

    public void setGenres(String genres) {
        genresText.setText(genres);
    }

    public void setOriginalName(String name) {
        if (name == null || name.isEmpty()) {
            linearLayout.removeView(originalName);
            linearLayout.removeView(originalNameText);
            return;
        }

        originalNameText.setText(name);
    }

    public void setCountries(String countries) {
        if (countries == null || countries.isEmpty()) {
            linearLayout.removeView(originalCountry);
            linearLayout.removeView(originalCountryText);
            return;
        }

        originalCountryText.setText(countries);
    }

    public void setStatus(String status) {
        statusText.setText(status);
    }

    public void setType(String type) {
        typeText.setText(type);
    }

    public void setDates(String first, String last) {
        firstAirDate.setText(first);
        lastAirDate.setText(last);
    }

    public void setCompanies(String companies) {
        companiesText.setText(companies);
    }

    public void setHomepage(String homepage) {
        homepageText.setText(homepage);
    }
}
CircularImageView
====
A checkable Android ImageView implementation that draws circular images with support for optional placeholder text. CircularTextView works with [Picasso][picasso] (tested with picasso:2.5.2) & [Glide][glide] (tested with glide:3.6.1).

![Cat](art/cat.png)

## Download

Download the latest version from [releases][1].

Or get it using Gradle:

````groovy
dependencies {
    compile 'com.subinkrishna:circularimageview:1.0.2'
}
````

Or Maven:

````xml
<dependency>
  <groupId>com.subinkrishna</groupId>
  <artifactId>circularimageview</artifactId>
  <version>1.0.2</version>
</dependency>
````


# Usage

**XML:**

````xml
<com.subinkrishna.widget.CircularImageView
    android:id="@+id/image"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:src="@drawable/c2"
    app:ci_borderWidth="2dp"
    app:ci_borderColor="@android:color/white"
    app:ci_placeholderBackgroundColor="@android:color/black"
    app:ci_placeholderText="CV"
    app:ci_placeholderTextSize="22sp"
    app:ci_placeholderTextColor="@android:color/white" />
````

**Java:**

````java
CircularImageView imageView = findViewById(R.id.image);
imageView.setBorderColor(Color.WHITE);
imageView.setBorderWidth(TypedValue.COMPLEX_UNIT_DIP, 2);
imageView.setPlaceholder("CV", Color.BLACK, Color.WHITE);
imageView.setPlaceholderTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
````

**Custom Attributes**

* `ci_borderWidth` (default: `0`)
* `ci_borderColor` (default: `#FFFFFFFF`)
* `ci_placeholderText`
* `ci_placeholderTextSize` (default: `0`)
* `ci_placeholderTextColor` (default: `#FF000000`)
* `ci_placeholderBackgroundColor` (default: `#FFDDDDDD`)
* `ci_checked` (default: `false`)
* `ci_checkedStateBackgroundColor` (default: `#FFBBBBBB`)

**Java Methods**

* `setBorderWidth(int unit, int size)`
* `setBorderColor(@ColorInt int color)`
* `setPlaceholder(String text)`
* `setPlaceholder(String text, @ColorInt int backgroundColor, @ColorInt int textColor)`
* `setPlaceholderTextSize(int unit, int size)`
* `setCheckedStateBackgroundColor(@ColorInt int backgroundColor)`

Methods implemented from `android.widget.Checkable`

* `isChecked()`
* `setChecked(boolean checked)`
* `toggle()`

## Samples

![Sample](screenshots/samples.png)

**#1 Using Picasso**

XML:

```` xml
<com.subinkrishna.widget.CircularImageView
    android:id="@+id/image1"
    android:layout_width="100dp"
    android:layout_height="100dp" />
````

Java:

````java
CircularImageView i1 = (CircularImageView) findViewById(R.id.image1);
Picasso.with(this)
    .load("https://raw.githubusercontent.com/subinkrishna/CircularImageView/master/art/cat_original.jpg")
    .placeholder(R.drawable.placeholder)
    .centerCrop()
    .resize(200, 200)
    .into(i1);
````

**#2 Using Glide - Error handling**

Placeholder image is shown in the sample since Glide tried to load an image from an invalid URL.

XML:

````xml
<com.subinkrishna.widget.CircularImageView
    android:id="@+id/image2"
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:ci_placeholderBackgroundColor="@android:color/holo_orange_light"
    app:ci_placeholderTextSize="42sp"
    app:ci_placeholderTextColor="#FFF"
    app:ci_placeholderText="CV" />

````

Java:

````java
CircularImageView i2 = (CircularImageView) findViewById(R.id.image2);
Glide.with(this)
    .load("http://invalid.url")
    .asBitmap()
    .error(R.drawable.placeholder)
    .into(i2);
````

**#3 Local asset**

````xml
<com.subinkrishna.widget.CircularImageView
    android:id="@+id/image3"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:src="@drawable/c1"
    app:ci_borderWidth="3dp"
    app:ci_borderColor="@android:color/holo_blue_dark"/>
````

**#4 Placeholder text with custom border & background**

Placeholder text is shown along with custom border & background when no bitmap is loaded.

````xml
<com.subinkrishna.widget.CircularImageView
    android:id="@+id/image5"
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:ci_placeholderBackgroundColor="@android:color/holo_orange_light"
    app:ci_placeholderTextSize="42sp"
    app:ci_placeholderTextColor="#FFF"
    app:ci_placeholderText="CV"/>
````

## Changelog

#### 1.0.2

* `CircularImageView` implements `android.widget.Checkable`

#### 1.0.1

* Renamed all custom attributes to suffix `ci_` to avoid conflicts

## Limitations & known issues

* Supports only `BitmapDrawable`s.
* CircularImageView doesn't resize bitmaps to match the view size.
* No support for animations. Please use `DrawableTypeRequest.asBitmap()` to make CircularImageView to work with [Glide][glide].

## License

    Copyright (C) 2015 Subinkrishna Gopi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: ../../releases
[picasso]: http://square.github.io/picasso/
[glide]: https://github.com/bumptech/glide
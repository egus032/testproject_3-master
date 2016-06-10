var gulp = require("gulp"),
        runSequence = require("run-sequence"),
        // inject
        mainBowerFiles = require("main-bower-files"),
        inject = require("gulp-inject"),
        // minify
        concat = require("gulp-concat"),
        uglify = require("gulp-uglify"),
        sourcemaps = require("gulp-sourcemaps"),
        htmlmin = require("gulp-htmlmin"),
        csso = require("gulp-csso"),
        ngAnnotate = require("gulp-ng-annotate"),
        // hints
        htmlhint = require("gulp-htmlhint"),
        csslint = require("gulp-csslint"),
        jshint = require("gulp-jshint"),
        //files
        path = require("path"),
        crypto = require("crypto"),
        fs = require("fs"),
        //utils
        gulpFilter = require("gulp-filter"),
        gulpif = require("gulp-if"),
        util = require("gulp-util");

// Paths
var dirs = {
    target: "../../../target/" + util.env.target,
    webapp: "../webapp",
    static: "static"
},
mask = {
    js: ["js/vendors/**/*.js",
        "app/app.js",
        "app/*.js",
        "app/**/*.js",
        "js/*.js"
    ],
    css: ["css/*.css", "app/common/**/*.css"],
    html: ["app/**/*.html"]
};

// Help
var isProduction = (util.env.asset == "production"),
        isDevelopment = (util.env.asset == "development"),
        getSources = function () {
            var pathNames = mask.js.concat(mask.css).concat(mask.html);
            for (pathName in pathNames) {
                pathNames[pathName] = path.join(dirs.webapp, dirs.static, pathNames[pathName]);
            }
            return pathNames;
        },
        copyBowerFiles = function () {
            return gulp.src(mainBowerFiles())
                    .pipe(gulp.dest(path.join(dirs.target, dirs.static, "/bower_components")));
        },
        hash = function hash(filePath) {
            return crypto
                    .createHash("sha1")
                    .update(fs.readFileSync(path.join(filePath), {encoding: "utf8"}))
                    .digest("hex")
        };



gulp.task("inject", function () {
    var transform = function (filepath, file, i, length) {
        if (filepath.slice(-3) === ".js") {
            var async = filepath.slice(-13) === "production.js";
            filepath += "?v=" + hash(path.join(dirs.target, filepath));
            return "<script src=\"" + filepath + "\"" + (async ? " async" : "") + "></script>";
        }

        filepath += "?v=" + hash(path.join(dirs.target, filepath));
        return inject.transform.apply(inject.transform, arguments);
    },
            getOptions = function (name, ignorePath) {
                return {ignorePath: ignorePath, addRootSlash: false, name: name, transform: transform};
            },
            getProductionSources = function () {
                return [path.join(dirs.target, dirs.static, "production.js"), path.join(dirs.target, dirs.static, "css/production.css")];
            };

    var target = gulp.src([path.join(dirs.target, "**/stylesheets.ftl"), path.join(dirs.target, "**/javascripts.ftl")]);

    target
            .pipe(gulpif(isDevelopment, inject(copyBowerFiles(), getOptions("bower", dirs.target))))
            .pipe(gulpif(isDevelopment, inject(gulp.src(getSources(), {read: false}), getOptions("inject", dirs.webapp))))
            .pipe(gulpif(isProduction, inject(gulp.src(getProductionSources(), {read: false}), getOptions("inject", dirs.target))))
            .pipe(gulp.dest(dirs.target));
});

// Sequences
gulp.task("development", function () {
    runSequence(["validate-client", "inject"]);
});

gulp.task("production", function () {
    runSequence(["validate-client", "html-compress", "concat-compress-js", "concat-compress-css"], "inject");
});

// Tasks
gulp.task("concat-compress-css", function () {
    return gulp.src(mainBowerFiles().concat(getSources()))
            .pipe(gulpFilter("**/*.css"))
            .pipe(concat("css/production.css"))
            .pipe(csso())
            .pipe(gulp.dest(path.join(dirs.target, dirs.static)));
});

gulp.task("concat-compress-js", function () {
    return gulp.src(mainBowerFiles().concat(getSources()))
            .pipe(gulpFilter("**/*.js"))
            .pipe(sourcemaps.init())
            .pipe(ngAnnotate())
            .pipe(concat("production.js"))
            .pipe(uglify())
            .pipe(sourcemaps.write("./"))
            .pipe(gulp.dest(path.join(dirs.target, dirs.static)));
});

gulp.task("html-compress", function () {
    return; // remove, if html views doesn't use Freemarker
    return gulp.src(getSources())
            .pipe(gulpFilter("**/*.html"))
            .pipe(htmlmin({collapseWhitespace: false}))
            .pipe(gulp.dest(path.join(dirs.target, dirs.static, "app")));
});

gulp.task("validate-client", function () {
    runSequence(["html-hint", "js-hint", "css-lint"]);
});

gulp.task("html-hint", function () {
    return gulp.src(getSources())
            .pipe(gulpFilter("**/*.html"))
            .pipe(htmlhint({"doctype-first": false}))
            .pipe(htmlhint.reporter());
});

gulp.task("js-hint", function () {
    return gulp.src(getSources())
            .pipe(gulpFilter("**/*.js"))
            .pipe(jshint({expr: true}))
            .pipe(jshint.reporter("jshint-stylish"));
});

gulp.task("css-lint", function () {
    return gulp.src(getSources())
            .pipe(gulpFilter("**/*.css"))
            .pipe(csslint({
                "box-sizing": false,
                "adjoining-classes": false,
                "fallback-colors": false,
                "unqualified-attributes": false,
                "important": false
            }))
            .pipe(csslint.reporter());
});

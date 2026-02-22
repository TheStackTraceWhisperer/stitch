#!/bin/bash

# Script to recursively traverse the project and generate a markdown file
# with all source files embedded as codeblocks

OUTPUT_FILE="${1:-project_contents.md}"
PROJECT_ROOT="${2:-.}"

# File extensions to include
EXTENSIONS=("java" "xml" "md" "txt" "properties" "gradle" "sh" "py" "json" "yaml" "yml")

# Directories to exclude
EXCLUDE_DIRS=("target" ".git" ".idea" "node_modules" ".gradle" "build")

# Function to check if a directory should be excluded
should_exclude() {
    local dir="$1"
    for exclude in "${EXCLUDE_DIRS[@]}"; do
        if [[ "$dir" == *"$exclude"* ]]; then
            return 0
        fi
    done
    return 1
}

# Function to get file extension
get_extension() {
    local filename="$1"
    echo "${filename##*.}"
}

# Function to check if file should be included
should_include() {
    local filename="$1"
    local ext=$(get_extension "$filename")
    for file_ext in "${EXTENSIONS[@]}"; do
        if [[ "$ext" == "$file_ext" ]]; then
            return 0
        fi
    done
    return 1
}

# Function to get language for syntax highlighting
get_language() {
    local ext="$1"
    case "$ext" in
        java) echo "java" ;;
        xml) echo "xml" ;;
        md) echo "markdown" ;;
        properties) echo "properties" ;;
        gradle) echo "gradle" ;;
        sh) echo "bash" ;;
        py) echo "python" ;;
        json) echo "json" ;;
        yaml|yml) echo "yaml" ;;
        *) echo "text" ;;
    esac
}

# Initialize output file
> "$OUTPUT_FILE"

echo "# Project Contents" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "Generated on: $(date)" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "---" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Counter for files processed
file_count=0

# Traverse directory tree
find "$PROJECT_ROOT" -type f | sort | while read -r filepath; do
    # Get relative path
    rel_path="${filepath#$PROJECT_ROOT/}"

    # Check if we should exclude this path
    if should_exclude "$rel_path"; then
        continue
    fi

    # Get filename and extension
    filename=$(basename "$filepath")
    ext=$(get_extension "$filename")

    # Check if we should include this file
    if ! should_include "$filename"; then
        continue
    fi

    # Get syntax highlighting language
    lang=$(get_language "$ext")

    # Add file to markdown
    echo "## \`$rel_path\`" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    echo "\`\`\`\`$lang" >> "$OUTPUT_FILE"
    cat "$filepath" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    echo "\`\`\`\`" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    echo "---" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"

    ((file_count++))
done

echo "✓ Generated $OUTPUT_FILE with file contents"


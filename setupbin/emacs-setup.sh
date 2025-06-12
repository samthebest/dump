#!/bin/bash

brew install emacs
rustup component add rust-analyzer

# Create init.el if it doesn't exist
mkdir -p ~/.emacs.d

# MANUAL SHIT
# Open Terminal > Preferences > Profiles > Keyboard
# Check “Use Option as Meta key”

cat <<EOF > ~/.emacs.d/init.el
;;; Hello

;; Initialize package sources
(require 'package)
(setq package-enable-at-startup nil) ;; do not auto-load packages before init.el runs
(add-to-list 'package-archives '("gnu" . "https://elpa.gnu.org/packages/"))
(add-to-list 'package-archives '("melpa" . "https://melpa.org/packages/"))

(package-initialize)

(unless package-archive-contents
  (package-refresh-contents))

;; Ensure use-package is installed
(unless (package-installed-p 'use-package)
  (package-install 'use-package))

(require 'use-package)

(setq use-package-always-ensure t)

;; Better prompts
; (use-package vertico
;   :init
;   (vertico-mode))

;; Had to run `git clone https://github.com/minad/vertico.git ~/.emacs.d/vertico`
(add-to-list 'load-path "~/.emacs.d/vertico")
(require 'vertico)
(vertico-mode 1)

;; Rust mode
(use-package rust-mode
  :ensure t
  :mode "\\.rs\\'"
  :config)

;; Cargo minor mode - integrates cargo commands
(use-package cargo
  :ensure t
  :hook (rust-mode . cargo-minor-mode))

;; Flycheck for syntax checking
(use-package flycheck
  :ensure t
  :hook ((prog-mode . flycheck-mode)
         (lsp-mode . flycheck-mode)))

;; LSP mode for rust-analyzer
(use-package lsp-mode
  :ensure t
  :hook (rust-mode . lsp)
  :config
  (setq lsp-rust-analyzer-cargo-watch-command "clippy"
        lsp-diagnostics-provider :auto
        lsp-idle-delay 1.0
        lsp-rust-server 'rust-analyzer))

(setq lsp-log-io t)


;; Auto complete
(use-package company
  :ensure t
  :hook (after-init . global-company-mode)
  :bind (:map company-active-map
              ("<tab>" . company-complete-selection)
              ("TAB" . company-complete-selection))
        (:map company-mode-map
              ("<tab>" . company-indent-or-complete-common)
              ("TAB" . company-indent-or-complete-common)))

(setq tab-always-indent 'complete)

;; Alt Enter
(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "M-RET") #'lsp-execute-code-action))

;; Reformat buffer
(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "C-c C-f") #'lsp-format-buffer))

;; Rename
(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "C-c <f6>") #'lsp-rename))

;; Find usages
(global-set-key (kbd "C-c <f7>") 'lsp-find-references)

;; Inline variable
(defun lsp-rust-inline-action ()
  "Execute the Rust Analyzer inline refactor code action."
  (interactive)
  (lsp-request-async
   "textDocument/codeAction"
   (lsp--text-document-code-action-params)
   (lambda (actions)
     (let ((inline-action
            (seq-find (lambda (action)
                        (string= (gethash "kind" action) "refactor.inline"))
                      actions)))
       (if inline-action
           (lsp-execute-code-action inline-action)
         (message "No inline action available here"))))
   :error-handler #'lsp--error
   :mode 'detached))

(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "M-v") #'lsp-rust-inline-action))

;; Add `pub` to fields
(defun rust-publicize-struct-fields ()
  "Add 'pub' modifier to all struct fields that don't already have it."
  (interactive)
  (save-excursion
    (let ((struct-start (progn
                          ;; Find the beginning of the struct
                          (re-search-backward "\\bstruct\\b" nil t)
                          (point)))
          (struct-end (progn
                        ;; Find the opening brace
                        (re-search-forward "{" nil t)
                        (let ((brace-start (1- (point))))
                          ;; Find the matching closing brace
                          (goto-char brace-start)
                          (forward-sexp)
                          (point)))))
      ;; Go back to struct body
      (goto-char struct-start)
      (re-search-forward "{" nil t)
      ;; Process each line in the struct body
      (while (< (point) struct-end)
        (beginning-of-line)
        (when (looking-at "\\s-+\\([a-zA-Z_][a-zA-Z0-9_]*\\):")
          ;; This looks like a field, check if it already has pub
          (unless (looking-at "\\s-+pub\\s-")
            ;; Add pub before the field name
            (re-search-forward "\\s-+" nil t)
            (insert "pub ")))
        (forward-line 1)))))

;; Bind it to a key
(define-key rust-mode-map (kbd "C-c p") 'rust-publicize-struct-fields)

;; Move file to package
(defun my/move-rust-file-to-package ()
  "Move a Rust file selected in Treemacs to another package (directory),
and update `mod.rs` in both source and destination directories."
  (interactive)
  (require 'treemacs)
  (let* ((file (treemacs-node-at-point))
         (file-path (if (stringp file) file (treemacs-button-get file :path))))
    (if (and file-path (string-match-p "\\.rs\\'" file-path))
        (let* ((target-dir (read-directory-name "Move to directory: "))
               (filename (file-name-nondirectory file-path))
               (source-dir (file-name-directory file-path))
               (target-path (expand-file-name filename target-dir))
               (mod-decl (concat "pub mod " (file-name-sans-extension filename) ";")))
          ;; Move the file
          (rename-file file-path target-path 1)
          ;; Update mod.rs in source dir
          (let ((source-mod (expand-file-name "mod.rs" source-dir)))
            (when (file-exists-p source-mod)
              (with-temp-buffer
                (insert-file-contents source-mod)
                (goto-char (point-min))
                (when (re-search-forward (concat "^" (regexp-quote mod-decl)) nil t)
                  (beginning-of-line)
                  (kill-line 1))
                (write-region (point-min) (point-max) source-mod))))
          ;; Update mod.rs in target dir
          (let ((target-mod (expand-file-name "mod.rs" target-dir)))
            (unless (file-exists-p target-mod)
              (with-temp-buffer
                (write-region (point-min) (point-max) target-mod)))
            (with-temp-buffer
              (insert-file-contents target-mod)
              (goto-char (point-max))
              (unless (re-search-backward (concat "^" (regexp-quote mod-decl)) nil t)
                (goto-char (point-max))
                (insert mod-decl "\n"))
              (write-region (point-min) (point-max) target-mod))))
      (message "Please select a .rs file in Treemacs."))))

(global-set-key (kbd "C-x <f6>") #'my/move-rust-file-to-package)

;;
(global-set-key (kbd "C-c e") #'next-error)

;; Supress unnecessary doc string warnings
(when (listp byte-compile-warnings)
  (setq byte-compile-warnings (remove 'docstrings byte-compile-warnings)))

;; Make it save buffers (layout)                     
(desktop-save-mode 1)

;; Globally save buffers (layout) TODO Make it work on a per-project basis
(setq desktop-path (list user-emacs-directory)) ; usually ~/.emacs.d/
(setq desktop-dirname user-emacs-directory)
(setq desktop-base-file-name "emacs.desktop")
(setq desktop-auto-save-timeout 10)  ; auto-save desktop every 2 seconds

(add-hook 'kill-emacs-hook #'desktop-save-in-desktop-dir)

(when (file-exists-p (expand-file-name "emacs.desktop" user-emacs-directory))
  (desktop-read user-emacs-directory))

(setq desktop-files-not-to-save nil)  ; save all buffers

(add-hook 'emacs-startup-hook 'treemacs)

;; Automatically load changes on disk
(global-auto-revert-mode 1)

;; Also revert Dired and other buffers
(setq global-auto-revert-non-file-buffers t)

;; Be quiet about it
(setq auto-revert-verbose nil)

;; Smooth scrolling
(setq scroll-margin 10)           ;; start scrolling when 5 lines from edge
(setq scroll-conservatively 101)  ;; don't recenter cursor unnecessarily
(setq scroll-step 1)              ;; scroll one line at a time
(setq auto-window-vscroll nil)    ;; disable slow pixel-based scrolling

;; Quickly reload init.el
(defun reload-init-file ()
  "Reload the Emacs init file."
  (interactive)
  (load user-init-file))

(global-set-key (kbd "C-c r") 'reload-init-file)
(global-set-key (kbd "C-c C-r") 'reload-init-file)

;; Edit init.el
(defun cf ()
  "Edit the `user-init-file`, which is usually ~/.emacs.d/init.el."
  (interactive)
  ;; (find-file user-init-file))
  (let ((buffer (find-file-noselect user-init-file)))
    (switch-to-buffer buffer)))

;; Directory viewer
(setq treemacs-width 70)

(use-package treemacs
  :ensure t
  :defer t
  :bind
  ("C-c t" . treemacs)
  :config)

;; Make copy paste actually work (in GUI apparently (not tested))
(setq select-enable-clipboard t)

;; Make copy paste actually work in Emacs
(setq interprogram-cut-function
      (lambda (text &optional _push)
        (let ((process-connection-type nil))
          (let ((proc (start-process "pbcopy" "*Messages*" "pbcopy")))
            (process-send-string proc text)
            (process-send-eof proc)))))

(setq interprogram-paste-function
      (lambda ()
        (let ((text (shell-command-to-string "pbpaste")))
          (unless (string= text "")
            text))))

;; Delete line
(global-set-key (kbd "C-c x") 'kill-whole-line)

;; Copy text
(global-set-key (kbd "C-c c") 'kill-ring-save)

;; Dupe line
(defun duplicate-line()
  "Duplicate the current line."
  (interactive)
  (move-beginning-of-line 1)
  (kill-ring-save (point) (line-end-position))
  (end-of-line)
  (open-line 1)
  (next-line 1)
  (yank))

(global-set-key (kbd "C-c d") 'duplicate-line)
(global-set-key (kbd "C-c C-d") 'duplicate-line)

;; Auto save
(setq auto-save-timeout 1)
(setq auto-save-interval 1)
(setq auto-save-default t)

(run-with-idle-timer 1 t (lambda () (save-some-buffers t)))

;; Remove indentation
(defun unindent-region-by-one-level (start end)
  "Unindent selected region by one standard indent level for the current major mode."
  (interactive "r")
  (let ((indent (or (and (boundp 'c-basic-offset) c-basic-offset)
                    (and (boundp 'lisp-body-indent) lisp-body-indent)
                    (and (boundp 'rust-indent-offset) rust-indent-offset)
                    tab-width
                    4)))
    (indent-rigidly start end (- indent))))

(global-set-key (kbd "<backtab>") 'unindent-region-by-one-level)


;; Add indentation
(defun indent-region-by-one-level ()
  "Indent selected region by one standard indent level for the current major mode."
  (interactive)
  (let ((indent (or (and (boundp 'c-basic-offset) c-basic-offset) ; c-like modes
                    (and (boundp 'lisp-body-indent) lisp-body-indent) ; elisp
                    (and (boundp 'rust-indent-offset) rust-indent-offset) ; rust-mode
                    tab-width ; fallback
                    4))) ; default fallback
    (indent-rigidly (region-beginning) (region-end) indent)))

(global-set-key (kbd "C-TAB") 'indent-region-by-one-level)

;; Delete word but don't copy paste it
(defun backward-delete-word (arg)
  "Delete previous word without adding to kill ring."
  (interactive "p")
  (delete-region (point) (progn (backward-word arg) (point))))

;; NOTE this overrides the default
(global-set-key (kbd "M-DEL") 'backward-delete-word)

;; Delete whitespace
(defun my/delete-whitespace-before-cursor ()
  "Delete all horizontal whitespace before point."
  (interactive)
  (let ((start (point)))
    (skip-chars-backward " \t\n")
    (delete-region (point) start)))

(global-set-key (kbd "C-c w") #'my/delete-whitespace-before-cursor)

;; Brute force make selection fucking work
(defun my/keep-region-active ()
  "Keep region active after most commands, except when cutting, copying, or deleting."
  (when (and (region-active-p)
             (not (memq this-command
                        '(kill-region
                          kill-ring-save
                          clipboard-kill-region
                          clipboard-kill-ring-save
                          delete-region
                          delete-char
                          backward-delete-char
                          delete-forward-char
                          delete-backward-char))))
    (setq deactivate-mark nil)))

(add-hook 'post-command-hook #'my/keep-region-active)

;; Find file in git repo (or project)
(global-set-key (kbd "C-x n") #'project-find-file)
(global-set-key (kbd "C-c n") #'project-find-file)

;; Type #
(global-set-key (kbd "C-c 3") (lambda () (interactive) (insert "#")))

;; Undo (easier to remember)
(global-set-key (kbd "C-c u") 'undo)
(global-set-key (kbd "C-c C-u") 'undo)

;; For Emacs 26 and later
(global-display-line-numbers-mode t)

;; Move a line
(defun move-line-up ()
  "Move the current line up."
  (interactive)
  (transpose-lines 1)
  (forward-line -2))

(defun move-line-down ()
  "Move the current line down."
  (interactive)
  (forward-line 1)
  (transpose-lines 1)
  (forward-line -1))

(global-set-key (kbd "C-c <up>") 'move-line-up)
(global-set-key (kbd "C-c <down>") 'move-line-down)

;; Multi-cursors
(require 'multiple-cursors)

(use-package multiple-cursors
  :ensure t
  :bind (("C-c g" . mc/mark-next-like-this)
         ("C-c h" . mc/mark-previous-like-this)
         ("C-c a" . mc/mark-all-like-this)))

(global-set-key (kbd "C-c 8") 'mc/edit-lines)
(global-set-key (kbd "C-c C-g") 'mc/mark-next-like-this)

;; Paste multiple cursors
(global-set-key (kbd "C-c v") (lambda () (interactive) (yank 4)))

;; Find text
(defun my-project-search ()
    "Search project files and select with arrow keys."
    (interactive)
    (let* ((search-term (read-string "Search for: "))
           (default-directory (or (locate-dominating-file default-directory ".git") default-directory))
           (files (split-string 
                   (shell-command-to-string 
                    (format "grep -r -l --include='*.rs' --include='*.toml' --include='*.md' -F '%s' ." search-term))
                   "\n" t)))
      (if files
          (find-file (completing-read "Select file: " files))
        (message "No matches found"))))

  (global-set-key (kbd "C-c f") 'my-project-search)

;; Make grep results navigable
(eval-after-load 'grep
  '(progn
     (define-key grep-mode-map (kbd "n") 'next-error)
     (define-key grep-mode-map (kbd "p") 'previous-error)
     (define-key grep-mode-map (kbd "RET") 'compile-goto-error)))

;; SHIT NOT WORK
; (global-set-key (kbd "C-c f") 'helm-do-ag)
; (global-set-key (kbd "C-c f")
;   (lambda (text)
;     (interactive "sSearch text: ")
;     (grep (format "grep -l -r --exclude-dir=target '%s' ." text))))
; (use-package ag
;     :ensure t
;     :config
;     (global-set-key (kbd "C-c f") 'ag-project))
 ; (use-package counsel
 ;    :ensure t
 ;    :config
 ;    (global-set-key (kbd "C-c f") 'counsel-rg))

;; Comment current line
(defun comment-or-uncomment-current-line ()
  "Comment or uncomment the current line."
  (interactive)
  (let ((beg (line-beginning-position))
        (end (line-end-position)))
    (comment-or-uncomment-region beg end)))

(global-set-key (kbd "C-c ;") #'comment-or-uncomment-current-line)

;; Save backups to
(setq backup-directory-alist
      `(("." . ,(concat user-emacs-directory ".emacs-backups"))))

;;
;; Treemacs
;;
(with-eval-after-load 'treemacs
  (define-key treemacs-mode-map (kbd "a") #'treemacs-create-file)  ;; We can also "c f" or "c d"
  (define-key treemacs-mode-map (kbd "+") #'treemacs-create-dir))

;;
;; RUST Specific shortcuts
;;
(defun my/treemacs-create-module (module-name)
  "Create a new Rust module named MODULE-NAME in the Treemacs-selected directory.
Also appends `pub mod <name>;` to mod.rs in that directory."
  (interactive "sModule name: ")
  (require 'treemacs)
  (let* ((btn (treemacs-current-button))
         (path (treemacs-button-get btn :path))
         (dir (if (file-directory-p path)
                  path
                (file-name-directory path)))
         (new-file (expand-file-name (concat module-name ".rs") dir))
         (mod-file (expand-file-name "mod.rs" dir)))
    ;; Create the new file if it doesn't exist
    (unless (file-exists-p new-file)
      (with-temp-buffer (write-file new-file)))
    ;; Add line to mod.rs
    (with-current-buffer (find-file-noselect mod-file)
      (goto-char (point-max))
      (insert (format "\npub mod %s;" module-name))
      (save-buffer))
    (message "Created %s and updated mod.rs" new-file)))

(global-set-key (kbd "C-x m") #'my/treemacs-create-module)

;; Create directory
(defun my/treemacs-create-rust-dir-with-mod ()
  "Create a new directory at the current point in Treemacs,
and update the parent's mod.rs with `pub mod <dir>;`."
  (interactive)
  (require 'treemacs)
  (let* ((node (treemacs-node-at-point))
         (path (if (stringp node) node (treemacs-button-get node :path)))
         (is-dir (file-directory-p path))
         (parent-dir (if is-dir path (file-name-directory path)))
         (new-dir-name (read-string "New Rust module directory name: "))
         (new-dir-path (expand-file-name new-dir-name parent-dir))
         (mod-decl (concat "pub mod " new-dir-name ";"))
         (mod-file (expand-file-name "mod.rs" parent-dir)))
    (if (file-exists-p new-dir-path)
        (message "Directory already exists.")
      (make-directory new-dir-path)
      ;; Create the new mod.rs file in the new directory
      (let ((new-mod (expand-file-name "mod.rs" new-dir-path)))
        (unless (file-exists-p new-mod)
          (with-temp-buffer
            (insert "// Submodule: " new-dir-name "\n")
            (write-region (point-min) (point-max) new-mod))))
      ;; Update parent mod.rs
      (when (file-exists-p mod-file)
        (with-temp-buffer
          (insert-file-contents mod-file)
          (goto-char (point-max))
          (unless (re-search-backward (concat "^" (regexp-quote mod-decl)) nil t)
            (goto-char (point-max))
            (insert mod-decl "\n"))
          (write-region (point-min) (point-max) mod-file)))
      (message "Created directory and updated mod.rs."))))

(global-set-key (kbd "C-x p") #'my/treemacs-create-rust-dir-with-mod)

;; Rename file
(defun my/treemacs-rename-file-and-update-mod ()
  "Rename the file at point in Treemacs and update parent mod.rs accordingly."
  (interactive)
  (require 'treemacs)
  (let* ((node (treemacs-node-at-point))
         (old-path (if (stringp node) node (treemacs-button-get node :path))))
    (if (not (file-regular-p old-path))
        (user-error "Not a regular file")
      (let* ((old-filename (file-name-nondirectory old-path))
             (parent-dir (file-name-directory old-path))
             (mod-file (expand-file-name "mod.rs" parent-dir))
             (new-name (read-string (format "Rename %s to: " old-filename)))
             (new-path (expand-file-name new-name parent-dir))
             (old-mod-name (file-name-sans-extension old-filename))
             (new-mod-name (file-name-sans-extension new-name)))
        ;; Rename the file
        (rename-file old-path new-path 1)
        ;; Update mod.rs if it exists
        (when (file-exists-p mod-file)
          (with-temp-buffer
            (insert-file-contents mod-file)
            ;; Remove old mod declaration line (pub mod old_mod_name;)
            (goto-char (point-min))
            (while (re-search-forward
                    (format "^\\s-*pub mod %s;\\s-*$" (regexp-quote old-mod-name))
                    nil t)
              (replace-match ""))
            ;; Add new mod declaration if not present
            (goto-char (point-min))
            (unless (re-search-forward
                     (format "^\\s-*pub mod %s;\\s-*$" (regexp-quote new-mod-name))
                     nil t)
              (goto-char (point-max))
              (unless (bolp) (insert "\n"))
              (insert (format "pub mod %s;\n" new-mod-name)))
            ;; Clean up multiple blank lines
            (goto-char (point-min))
            (while (re-search-forward "\n\\{3,\\}" nil t)
              (replace-match "\n\n"))
            ;; Save mod.rs
            (write-region (point-min) (point-max) mod-file)))
        ;; Refresh treemacs
        (treemacs-refresh)
        (message "Renamed %s to %s and updated mod.rs" old-filename new-name)))))

(global-set-key (kbd "C-x 6") #'my/treemacs-rename-file-and-update-mod)

;; Break string literals
(defun rust-split-string-at-point-with-concat ()
  "Split the current Rust string literal at point using concat!(...), preserving indentation."
  (interactive)
  (save-excursion
    (let* ((point (point))
           (string-start (save-excursion
                           (search-backward "\"" nil t)))
           (string-end (save-excursion
                         (search-forward "\"" nil t)))
           (line-indent (save-excursion
                          (back-to-indentation)
                          (buffer-substring (line-beginning-position) (point)))))
      (if (and string-start string-end
               (> point string-start)
               (< point string-end))
          (let ((left (buffer-substring-no-properties (1+ string-start) point))
                (right (buffer-substring-no-properties point (1- string-end))))
            ;; Delete the original string
            (goto-char string-start)
            (delete-region string-start string-end)
            ;; Insert the concat! block
            (insert (format "concat!(\n%s\"%s\",\n%s\"%s\"\n%s)"
                            line-indent
                            left
                            line-indent
                            right
                            line-indent)))
        (message "Cursor is not inside a string literal.")))))

(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "C-c RET") #'rust-split-string-at-point-with-concat))

;; Things we almost always want and always easy to derive
(defun rust-add-debug-partialeq-eq ()
  "Add #[derive(Debug, PartialEq, Eq, Hash)] above the current line"
  (interactive)
  (beginning-of-line)
  (open-line 1)
  (insert "#[derive(Debug, PartialEq, Eq, Hash)]")
  (forward-line))

(global-set-key (kbd "C-c D") 'rust-add-debug-partialeq-eq)

;;
;; Auto Generated stuff
;;
(custom-set-variables
 ;; custom-set-variables was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 '(package-selected-packages
   '(ag cargo company deadgrep flycheck helm-ag lsp-ui multiple-cursors
	rust-mode treemacs vertico)))
(custom-set-faces
 ;; custom-set-faces was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 )
EOF

